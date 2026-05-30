package com.project.hexagonal.bid.infrastructure.listener;

import com.project.hexagonal.bid.application.contract.input.BidApplicationService;
import com.project.hexagonal.shared.events.offer.OfferCancelledEvent;
import com.project.hexagonal.shared.events.offer.OfferClosedEvent;
import com.project.hexagonal.shared.infrastructure.exception.EventListenerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfferClosedEventListener {

    private final BidApplicationService bidApplicationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClosed(OfferClosedEvent event) {
        try {
            log.info("Offer closed event received: {}", event.offerId());
            bidApplicationService.cancelBidsForOffer(event.offerId());
        } catch (RuntimeException e) {
            throw new EventListenerException("Error while handling offer closed event with id: " + event.offerId(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCancelled(OfferCancelledEvent event) {
        try {
            log.info("Offer cancelled event received: {}", event.offerId());
            bidApplicationService.cancelBidsForOffer(event.offerId());
        } catch (RuntimeException e) {
            throw new EventListenerException("Error while handling offer cancelled event with id: " + event.offerId(), e);
        }
    }
}
