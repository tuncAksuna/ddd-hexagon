package com.project.hexagonal.bid.infrastructure.listener;

import com.project.hexagonal.bid.core.valueobject.OfferProjectionEnum;
import com.project.hexagonal.bid.infrastructure.entity.OfferProjectionEntity;
import com.project.hexagonal.bid.infrastructure.repository.OfferProjectionJpaRepository;
import com.project.hexagonal.shared.application.contract.output.OutboxPersistencePort;
import com.project.hexagonal.shared.events.offer.OfferCancelledEvent;
import com.project.hexagonal.shared.events.offer.OfferClosedEvent;
import com.project.hexagonal.shared.events.offer.OfferPublishedEvent;
import com.project.hexagonal.shared.infrastructure.persistence.adapter.InboxPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfferProjectionSyncListener {

    private static final String CONSUMER = "bid-projection";

    private static final String ERROR_MESSAGE = "ERROR: Offer projection sync failed for offer {}. Bid filtering may be affected.";

    private final OfferProjectionJpaRepository repository;
    private final InboxPersistenceAdapter inboxAdapter;
    private final OutboxPersistencePort outboxPersistencePort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOfferPublished(OfferPublishedEvent event) {
        try {
            if (inboxAdapter.isEventAlreadyProcessed(event.getEventId(), CONSUMER)) {
                log.info("Event already processed (skipping duplicate): eventId={}", event.getEventId());
                return;
            }

            inboxAdapter.markEventStart(event.getEventId(), event.getEventType(), CONSUMER);
            log.info("Syncing offer projection: {} → PUBLISHED", event.offerId());
            repository.save(OfferProjectionEntity.builder()
                    .id(event.offerId())
                    .status(OfferProjectionEnum.PUBLISHED)
                    .build());

            inboxAdapter.markEventSuccess(event.getEventId(), CONSUMER);
            outboxPersistencePort.markProcessedByEventId(event.getEventId());
        } catch (RuntimeException e) {
            log.error(ERROR_MESSAGE, event.offerId(), e);
            inboxAdapter.markEventFailure(event.getEventId(), CONSUMER, e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOfferClosed(OfferClosedEvent event) {
        try {
            if (inboxAdapter.isEventAlreadyProcessed(event.getEventId(), CONSUMER)) {
                log.info("Event already processed (skipping duplicate): eventId={}", event.getEventId());
                return;
            }

            inboxAdapter.markEventStart(event.getEventId(), event.getEventType(), CONSUMER);
            log.info("Syncing offer projection: {} → CLOSED", event.offerId());
            repository.findById(event.offerId()).ifPresent(projection -> {
                projection.setStatus(OfferProjectionEnum.CLOSED);
                repository.save(projection);
            });

            inboxAdapter.markEventSuccess(event.getEventId(), CONSUMER);
            outboxPersistencePort.markProcessedByEventId(event.getEventId());
        } catch (RuntimeException e) {
            log.error(ERROR_MESSAGE, event.offerId(), e);
            inboxAdapter.markEventFailure(event.getEventId(), CONSUMER, e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOfferCancelled(OfferCancelledEvent event) {
        try {
            if (inboxAdapter.isEventAlreadyProcessed(event.getEventId(), CONSUMER)) {
                log.info("Event already processed (skipping duplicate): eventId={}", event.getEventId());
                return;
            }

            inboxAdapter.markEventStart(event.getEventId(), event.getEventType(), CONSUMER);
            log.info("Syncing offer projection: {} → CANCELLED", event.offerId());
            repository.findById(event.offerId()).ifPresent(projection -> {
                projection.setStatus(OfferProjectionEnum.CANCELLED);
                repository.save(projection);
            });

            inboxAdapter.markEventSuccess(event.getEventId(), CONSUMER);
            outboxPersistencePort.markProcessedByEventId(event.getEventId());
        } catch (RuntimeException e) {
            log.error(ERROR_MESSAGE, event.offerId(), e);
            inboxAdapter.markEventFailure(event.getEventId(), CONSUMER, e.getMessage());
        }
    }
}
