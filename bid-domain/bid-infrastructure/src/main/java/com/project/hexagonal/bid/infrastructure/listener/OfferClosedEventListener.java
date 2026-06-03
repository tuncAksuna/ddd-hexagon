package com.project.hexagonal.bid.infrastructure.listener;

import com.project.hexagonal.bid.application.contract.input.BidApplicationService;
import com.project.hexagonal.shared.core.exception.EventListenerException;
import com.project.hexagonal.shared.events.offer.OfferCancelledEvent;
import com.project.hexagonal.shared.events.offer.OfferClosedEvent;
import com.project.hexagonal.shared.infrastructure.persistence.adapter.EventFailureLogAdapter;
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
    private final EventFailureLogAdapter failureLog;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClosed(OfferClosedEvent event) {
        try {
            log.info("Offer closed event received: {}", event.offerId());
            bidApplicationService.cancelBidsForOffer(event.offerId());
        } catch (EventListenerException e) {
            log.error("CRITICAL: Bid cancel failed for offer {}. Event will be logged for manual intervention.", event.offerId(), e);
            failureLog.log(event.getClass().getSimpleName(), event.toString(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCancelled(OfferCancelledEvent event) {
        try {
            log.info("Offer cancelled event received: {}", event.offerId());
            bidApplicationService.cancelBidsForOffer(event.offerId());
        } catch (EventListenerException e) {
            log.error("CRITICAL: Bid cancel failed for offer {}. Event will be logged for manual intervention.", event.offerId(), e);
            failureLog.log(event.getClass().getSimpleName(), event.toString(), e);
        }
    }
}
