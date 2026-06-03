package com.project.hexagonal.offer.infrastructure.listener;

import com.project.hexagonal.offer.application.contract.input.OfferApplicationService;
import com.project.hexagonal.shared.core.exception.EventListenerException;
import com.project.hexagonal.shared.events.bid.BidAcceptedEvent;
import com.project.hexagonal.shared.infrastructure.persistence.adapter.EventFailureLogAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfferAcceptBidEventListener {

    private final OfferApplicationService offerApplicationService;
    private final EventFailureLogAdapter failureLog;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BidAcceptedEvent event) {
        try {
            log.info("Bid accepted event received: bidId={}, offerId={}", event.bidId(), event.offerId());
            offerApplicationService.updateStatusOnBidAccepted(event.offerId());
        } catch (EventListenerException e) {
            log.error("ERROR: Offer status update failed for offer {} when bid {} was accepted.",
                    event.offerId(), event.bidId(), e);
            failureLog.log(event.getClass().getSimpleName(), event.toString(), e);
        }
    }
}
