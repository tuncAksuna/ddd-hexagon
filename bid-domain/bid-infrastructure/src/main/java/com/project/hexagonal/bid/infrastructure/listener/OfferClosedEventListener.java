package com.project.hexagonal.bid.infrastructure.listener;

import com.project.hexagonal.bid.application.contract.input.BidApplicationService;
import com.project.hexagonal.shared.events.offer.OfferClosedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OfferClosedEventListener {

    private final BidApplicationService bidApplicationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OfferClosedEvent event) {
        bidApplicationService.cancelBidsForOffer(event.offerId());
    }
}
