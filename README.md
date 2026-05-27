# Hexagonal Architecture — Modular Monolith

A production-minded Java project built around strict hexagonal architecture and Domain-Driven Design principles. The goal was simple: build something that actually enforces the rules, not just talks about them. Every architectural decision here was made with one question in mind — *what happens when this needs to become a microservice?*

---

## What's in here

Two bounded contexts — `offer` and `bid` — living inside a single deployable Spring Boot application. They don't know about each other at the code level. They communicate only through domain events. The `shared` module provides the common contracts (base classes, event interfaces, custom annotations) without creating coupling between domains.

The `notification` module is scaffolded but not yet implemented. That's next.

---

## Module structure

```
hexagonal/
├── bootstrap/                  ← single entry point, wires everything together
├── shared/
│   ├── shared-core             ← AggregateRoot, BaseEntity, Money, DomainEvent interface
│   ├── shared-events           ← OfferClosedEvent, OfferPublishedEvent
│   ├── shared-application      ← @DomainService, @AppMapper, DomainEventPublisher port
│   ├── shared-api              ← shared web/security/validation config
│   └── shared-infrastructure   ← AbstractBaseEntity, @DomainMapper
├── offer-domain/
│   ├── offer-core              ← Offer aggregate, OfferStatus state machine
│   ├── offer-application       ← use cases: create, publish, cancel
│   ├── offer-api               ← REST controllers, request DTOs
│   └── offer-infrastructure    ← JPA entities, mappers, adapters, event publisher
├── bid-domain/
│   ├── bid-core                ← Bid aggregate, BidStatus state machine
│   ├── bid-application         ← use cases: applyBid, cancelBidsForOffer
│   ├── bid-api                 ← REST controllers, event listeners
│   └── bid-infrastructure      ← JPA entities, mappers, offer projection, adapters
└── notification/               ← (in progress)
```

The dependency rule: `infrastructure → application → core`. Nothing flows the other way. The `api` layer sits alongside `infrastructure` — both depend on `application`, neither knows about each other.

---

## Stack

- **Java 17**, **Spring Boot 3.5.x**
- **Spring Data JPA** + **PostgreSQL**
- **Lombok** — `@Builder`, `@Getter`, `@RequiredArgsConstructor`
- **Maven multi-module**

---

## A few things worth explaining

### Rich domain model

Business rules live inside the domain objects, not in service classes. `Offer` and `Bid` are aggregate roots that enforce their own invariants:

```java
public void close() {
    if (!(OfferStatus.PUBLISHED.equals(status) || OfferStatus.UPDATED.equals(status))) {
        throw new OfferDomainException("Cannot close an offer that is not published or updated!");
    }
    status = OfferStatus.CLOSED;
    domainEvents.add(new OfferClosedEvent(super.getId().getVal(), Instant.now()));
}
```

State transitions go through state machine enums (`OfferStatus`, `BidStatus`) with abstract methods per state. Terminal states throw `IllegalStateException` — no way to accidentally transition out of CLOSED or CANCELLED.

### Cross-domain communication

`bid-domain` needs to know if an offer is published before accepting a bid. But it can't import `offer-domain` code. The solution: `bid-infrastructure` maintains a local `offer_projection` table (just `id` and `status`), kept current by listening to offer domain events.

```
OfferApplicationServiceImpl.publish()
  → Offer.processStatus() emits OfferPublishedEvent
    → OfferProjectionSyncListener writes to offer_projection (bid's own DB table)

BidApplicationServiceImpl.applyBid()
  → reads from offer_projection via OfferProjectionPort
  → if not PUBLISHED → BidDomainException
```

When `offer-domain` fires `OfferClosedEvent`, two things happen independently:
- `OfferProjectionSyncListener` updates the projection to CLOSED
- `OfferClosedEventListener` calls `cancelBidsForOffer()`

Both are `@TransactionalEventListener(AFTER_COMMIT)` + `@Transactional(REQUIRES_NEW)` — they run after the offer transaction commits, in their own transactions.

### Keeping Spring out of the domain and application layers

`@DomainService`, `@AppMapper`, and `@DomainMapper` are plain Java annotations with no Spring imports. The `bootstrap` module registers them via `@ComponentScan` with `includeFilters`:

```java
@ComponentScan(includeFilters = @Filter(
    type = FilterType.ANNOTATION,
    value = { DomainService.class, AppMapper.class, DomainMapper.class }
))
@SpringBootApplication(scanBasePackages = "com.project.hexagonal")
public class OfferApplication { ... }
```

Same story for event publishing. The application layer declares a `DomainEventPublisher` port (pure Java interface). Infrastructure provides the Spring implementation:

```java
// shared-application — no Spring import
public interface DomainEventPublisher {
    void publish(DomainEvent event);
}

// offer-infrastructure — Spring lives here
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher publisher;
    public void publish(DomainEvent event) { publisher.publishEvent(event); }
}
```

### The ID problem with @Builder

Aggregate IDs come from `BaseEntity<ID>` which `@Builder` doesn't see (inherited field). So after building from a JPA entity, `setId()` is called explicitly:

```java
Offer offer = Offer.builder()
        .title(entity.getTitle())
        // ... other fields
        .build();
offer.setId(new OfferId(entity.getId()));  // must be separate
return offer;
```

Skipping this would cause a NPE the moment the domain tries to publish an event with its own ID. Took a while to catch.

---

## Flow overview

**Create and publish an offer:**
```
POST /api/offers          → create (status: DRAFT)
PUT  /api/offers/{id}     → publish (DRAFT → PUBLISHED, fires OfferPublishedEvent)
```

**Apply a bid:**
```
POST /api/bids/apply/{offerId}   → checks offer_projection (must be PUBLISHED)
                                 → saves Bid with status SUBMITTED
```

**Cancel an offer:**
```
DELETE /api/offers/{id}   → close() → CLOSED, fires OfferClosedEvent
                          → OfferClosedEventListener → cancelBidsForOffer()
                          → all non-CANCELLED bids transition to CANCELLED
```

---

## What's coming

The current event flow is in-process Spring events — solid for a monolith, but not resilient against partial failures. The next phase adds:

**Outbox Pattern** — instead of publishing events directly, the event is written to an `offer_outbox` / `bid_outbox` table within the same transaction as the domain state change. A scheduler picks up `PENDING` events and dispatches them, then marks them `PROCESSED`.

**Inbox / Idempotency** — on the consumer side, incoming event IDs get recorded so the same event can't be processed twice even if it's delivered more than once.

This is the missing piece before this architecture is actually production-safe. The domain event contracts are already designed with `eventId` support in mind.

---

## References that shaped this

- Alistair Cockburn — Hexagonal Architecture (the original)
- Lemi Orhan Ergin & Alican Akkuş — [YouTube talk](https://www.youtube.com/watch?v=jx49C380EgI)
- Vaughn Vernon — Implementing Domain-Driven Design

---

**Contact:** [aksuna.tunc@gmail.com](mailto:aksuna.tunc@gmail.com)  
**LinkedIn:** [Cem Tunç Aksuna](https://www.linkedin.com/in/cem-tunç-aksuna-012b31205)
