package com.project.hexagonal.bid.infrastructure.listener;

import com.project.hexagonal.bid.core.valueobject.OfferProjectionEnum;
import com.project.hexagonal.bid.infrastructure.entity.OfferProjectionEntity;
import com.project.hexagonal.bid.infrastructure.repository.OfferProjectionJpaRepository;
import com.project.hexagonal.shared.core.exception.EventListenerException;
import com.project.hexagonal.shared.events.offer.OfferCancelledEvent;
import com.project.hexagonal.shared.events.offer.OfferClosedEvent;
import com.project.hexagonal.shared.events.offer.OfferPublishedEvent;
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

    private static final String ERROR_MESSAGE = "ERROR: Offer projection sync failed for offer {}. Bid filtering may be affected.";

    private final OfferProjectionJpaRepository repository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOfferPublished(OfferPublishedEvent event) {
        try {
            log.info("Syncing offer projection: {} → PUBLISHED", event.offerId());
            repository.save(OfferProjectionEntity.builder()
                    .id(event.offerId())
                    .status(OfferProjectionEnum.PUBLISHED)
                    .build());
        } catch (EventListenerException e) {
            log.error(ERROR_MESSAGE, event.offerId(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOfferClosed(OfferClosedEvent event) {
        try {
            log.info("Syncing offer projection: {} → CLOSED", event.offerId());
            repository.findById(event.offerId()).ifPresent(projection -> {
                projection.setStatus(OfferProjectionEnum.CLOSED);
                repository.save(projection);
            });
        } catch (EventListenerException e) {
            log.error(ERROR_MESSAGE, event.offerId(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOfferCancelled(OfferCancelledEvent event) {
        try {
            log.info("Syncing offer projection: {} → CANCELLED", event.offerId());
            repository.findById(event.offerId()).ifPresent(projection -> {
                projection.setStatus(OfferProjectionEnum.CANCELLED);
                repository.save(projection);
            });
        } catch (EventListenerException e) {
            log.error(ERROR_MESSAGE, event.offerId(), e);
        }
    }
}
