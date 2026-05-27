package com.project.hexagonal.bid.infrastructure.listener;

import com.project.hexagonal.bid.infrastructure.entity.OfferProjectionEntity;
import com.project.hexagonal.bid.core.valueobject.OfferProjectionEnum;
import com.project.hexagonal.bid.infrastructure.repository.OfferProjectionJpaRepository;
import com.project.hexagonal.shared.events.offer.OfferClosedEvent;
import com.project.hexagonal.shared.events.offer.OfferPublishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OfferProjectionSyncListener {

    private final OfferProjectionJpaRepository repository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOfferPublished(OfferPublishedEvent event) {
        repository.save(OfferProjectionEntity.builder()
                .id(event.offerId())
                .status(OfferProjectionEnum.PUBLISHED)
                .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOfferClosed(OfferClosedEvent event) {
        repository.findById(event.offerId()).ifPresent(projection -> {
            projection.setStatus(OfferProjectionEnum.CLOSED);
            repository.save(projection);
        });
    }
}
