# Hexagonal Architecture — Modular Monolith

A production-minded Java project built around strict hexagonal architecture and Domain-Driven Design principles. Started with a simple question: *what happens when this needs to become a microservice?* That single constraint shaped every architectural decision.

Two bounded contexts — `offer` and `bid` — living in one Spring Boot application but never knowing about each other at the code level. They communicate exclusively through domain events. No direct dependencies, no database joins across domains. The `shared` module holds only the contracts (base classes, event definitions, annotations) needed by both.

---

## Module structure

```
hexagonal/
├── bootstrap/                  ← single entry point, @ComponentScan wires the domains
├── shared/
│   ├── shared-core             ← AggregateRoot, BaseEntity, Money, DomainEvent interface
│   ├── shared-events           ← OfferClosedEvent, OfferPublishedEvent, BidAcceptedEvent
│   ├── shared-application      ← @DomainService, @AppMapper, DomainEventPublisher port
│   ├── shared-api              ← GlobalExceptionHandler, error responses, shared web config
│   └── shared-infrastructure   ← AbstractBaseEntity, @DomainMapper, EventFailureLogAdapter
├── offer-domain/
│   ├── offer-core              ← Offer aggregate, OfferStatus state machine
│   ├── offer-application       ← use cases: create, publish, cancel, updateStatusOnBidAccepted
│   ├── offer-api               ← REST controllers
│   └── offer-infrastructure    ← JPA entities, mappers, adapters, OfferAcceptBidEventListener
├── bid-domain/
│   ├── bid-core                ← Bid aggregate, BidStatus state machine
│   ├── bid-application         ← use cases: applyBid, accept, cancelBidsForOffer
│   ├── bid-api                 ← REST controllers, OfferClosedEventListener (driving adapter)
│   └── bid-infrastructure      ← JPA entities, mappers, offer projection, OfferProjectionSyncListener
└── notification/               ← scaffolded, not yet implemented
```

The dependency rule: `infrastructure → application → core`. Nothing flows backward. The `api` layer (REST controllers) and `infrastructure` (JPA/DB) sit at the same level — both depend on `application`, neither on each other.

---

## Stack

- **Java 17**, **Spring Boot 3.5.x**
- **Spring Data JPA** + **PostgreSQL**
- **Lombok** — `@Builder`, `@Getter`, `@RequiredArgsConstructor`
- **Maven multi-module**

---

## A few things worth explaining

### Rich domain model

Business rules live inside domain objects, not service classes. `Offer` and `Bid` are aggregate roots with no getters for state — only methods that *do things*. State changes are validated before they happen:

```java
public void cancel() {
    if (OfferStatus.CANCELLED.equals(status)) return;
    status = status.cancel();
    registerEvent(new OfferCancelledEvent(super.getId().getVal(), Instant.now()));
}
```

State machine enums (`OfferStatus`, `BidStatus`) have abstract methods per state. Try to transition CLOSED → PUBLISHED? The enum throws `IllegalStateException`. No way to build an invalid state by accident.

Event registration happens inside the domain method that caused the state change. This way, code that calls `cancel()` doesn't have to remember to fire an event — it's built into the domain rule.

### Cross-domain communication without coupling

`bid-domain` needs to check if an offer is published, but it can't import `offer-domain` code. We solve this with **local projection**: `bid-infrastructure` keeps its own `offer_projection` table (just `id` and `status`), updated whenever offer events fire.

```
OfferApplicationServiceImpl.publish()
  → Offer.publishStatus() registers OfferPublishedEvent
  → publishEvents() dispatches to all listeners
    → OfferProjectionSyncListener receives it
      → saves/updates offer_projection in bid's database
                                   
BidApplicationServiceImpl.applyBid()
  → checks offer_projection (doesn't cross domain boundaries)
  → if PUBLISHED, accepts the bid
```

No cross-database queries. Each domain owns its data. Events are the only communication channel. This scales when you split into separate services later — the event contract is already in place.

### Event flow and error handling

Events are published **after** aggregate state is persisted. That way:
- If offer publishes successfully, the database commit is guaranteed
- Listener failures don't roll back the offer (eventual consistency)
- Cross-domain consistency arrives "eventually" — listeners fix it on retry

This is how `bid-domain` can cancel bids when an offer closes: even if the bid-cancel listener crashes temporarily, the offer is safely closed in its own database.

Listener errors are logged to `EventFailureLog` (a DLQ table), marked `PENDING` for manual review. Critical errors (bid cancellation, offer status updates) generate alerts. Non-critical errors (notifications, projection sync) log only — the system self-heals when the listener recovers.

### Keeping Spring out of domain and application layers

`@DomainService`, `@AppMapper`, `@DomainMapper` are plain Java annotations. No Spring imports. The bootstrap module finds them via `@ComponentScan`:

```java
@ComponentScan(includeFilters = @Filter(
    type = FilterType.ANNOTATION,
    value = { DomainService.class, AppMapper.class, DomainMapper.class }
))
@SpringBootApplication(scanBasePackages = "com.project.hexagonal")
public class OfferApplication { ... }
```

Event publishing works the same way. The application layer declares a `DomainEventPublisher` port (pure Java interface, lives in `shared-application`). Infrastructure implements it with Spring's `ApplicationEventPublisher`:

```java
// shared-application — pure Java
public interface DomainEventPublisher {
    void publishEvent(DomainEvent event);
}

// offer-infrastructure — Spring lives here
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    @Autowired private ApplicationEventPublisher publisher;
    public void publishEvent(DomainEvent event) { 
        publisher.publishEvent(event); 
    }
}
```

This way, if you need to swap Spring events for RabbitMQ later, you only change the infrastructure implementation. Domain and application layers don't care.

### The @Builder + inherited ID problem

`AggregateRoot` has a protected `id` field inherited from `BaseEntity`. Lombok's `@Builder` doesn't see inherited fields, so after building an aggregate from JPA data, we call `setId()` explicitly:

```java
Offer offer = Offer.builder()
        .title(entity.getTitle())
        .build();
offer.setId(new OfferId(entity.getId()));  // separate call
```

Skip this and you get a NPE when the aggregate tries to include its ID in an event. We learned this the hard way. The domain event processing path caught it, but it took a bit to track down where the null came from.

### The ID management in AggregateRoot

All domain events and queries need the aggregate ID, so `AggregateRoot` manages event registration centrally:

```java
public abstract class AggregateRoot<ID> extends BaseEntity<ID> {
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
    
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}
```

Aggregates never directly manage event lists. They call `registerEvent()`. The application service calls `publishEvents()` after persist. This makes the pattern consistent across all domains — no duplicate list management code.

---

## Flow overview

**Create and publish an offer:**
```
POST /api/offers              → Offer.validateAndInitialize() → DRAFT, saved
POST /api/offers/{id}/publish → Offer.processStatus() → PUBLISHED
                              → fires OfferPublishedEvent + OfferPublishedNotifyEvent
                              → listeners update offer_projection, send notifications
```

**Apply a bid:**
```
POST /api/bids/apply/{offerId}
  → check offer_projection (must be PUBLISHED)
  → Bid.validateAndInitialize() → SUBMITTED, saved
  → OfferProjectionSyncListener ensures projection exists
```

**Accept a bid:**
```
POST /api/bids/{bidId}/accept
  → Bid.accept() → ACCEPTED
  → fires BidAcceptedEvent + BidAcceptedNotifyEvent
  → OfferAcceptBidEventListener receives BidAcceptedEvent
  → Offer.updateStatus() → PUBLISHED → UPDATED
```

**Cancel an offer (closes all bids):**
```
DELETE /api/offers/{id}
  → Offer.cancel() → CANCELLED
  → fires OfferCancelledEvent
  → listeners update offer_projection + call cancelBidsForOffer()
  → all non-CANCELLED bids transition to CANCELLED, saved together
```

All event listeners run `@TransactionalEventListener(AFTER_COMMIT)` in their own transactions (`REQUIRES_NEW`). If a listener fails, it's logged to `event_failure_log` for manual review — the offer/bid state change is already persisted and safe.

---

## Error handling and observability

Failed listeners are logged to `event_failure_log` table with full stack traces. Status is `PENDING` — you can review, fix the underlying issue, and the listener will retry automatically when the aggregate is modified again.

The `GlobalExceptionHandler` converts domain exceptions to HTTP responses:
- `DomainException` (business rule violations) → **422 Unprocessable Entity**
- `SourceNotFoundException` → **404 Not Found**
- Validation failures → **400 Bad Request**
- Everything else → **500 Internal Server Error**

This way clients know whether they sent bad data (4xx) or something broke on the server (5xx).

---

## What's next

The current event flow is in-process Spring events with a manual DLQ fallback — solid for a monolith, resilient enough for production use if you watch the logs. The next phase (when you scale to microservices) adds:

**Outbox Pattern** — events get written to the same database transaction as the aggregate state change (`offer_outbox` / `bid_outbox` tables). A scheduler polls for `PENDING` events and dispatches them to message queues, then marks them `PROCESSED`. This guarantees no event is ever lost, even if the application crashes mid-publish.

**Inbox / Idempotency** — on the consumer side, listeners record the event ID they processed. If the same event is delivered twice (network hiccup), the listener skips it — your operations stay idempotent.

This is the final piece before splitting into separate services. The domain event contracts are already architected for it — they just need IDs.

---

## References that shaped this

- Alistair Cockburn — Hexagonal Architecture (the original)
- Lemi Orhan Ergin & Alican Akkuş — [YouTube talk](https://www.youtube.com/watch?v=jx49C380EgI)
- Vaughn Vernon — Implementing Domain-Driven Design

---

**Contact:** [aksuna.tunc@gmail.com](mailto:aksuna.tunc@gmail.com)  
**LinkedIn:** [Cem Tunç Aksuna](https://www.linkedin.com/in/cem-tunç-aksuna-012b31205)
