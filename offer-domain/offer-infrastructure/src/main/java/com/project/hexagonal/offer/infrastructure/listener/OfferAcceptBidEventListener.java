package com.project.hexagonal.offer.infrastructure.listener;

import com.project.hexagonal.offer.application.contract.input.OfferApplicationService;
import com.project.hexagonal.shared.application.contract.output.OutboxPersistencePort;
import com.project.hexagonal.shared.events.bid.BidAcceptedEvent;
import com.project.hexagonal.shared.infrastructure.persistence.adapter.EventFailureLogAdapter;
import com.project.hexagonal.shared.infrastructure.persistence.adapter.InboxPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfferAcceptBidEventListener {

    private static final String CONSUMER = "offer";

    private final OfferApplicationService offerApplicationService;
    private final InboxPersistenceAdapter inboxAdapter;
    private final EventFailureLogAdapter failureLog;
    private final OutboxPersistencePort outboxPersistencePort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void handle(BidAcceptedEvent event) {
        try {
            if (inboxAdapter.isEventAlreadyProcessed(event.getEventId(), CONSUMER)) {
                log.info("Event already processed (skipping duplicate): eventId={}", event.getEventId());
                return;
            }

            inboxAdapter.markEventStart(event.getEventId(), event.getEventType(), CONSUMER);
            log.info("Bid accepted event received: bidId={}, offerId={}", event.bidId(), event.offerId());
            offerApplicationService.updateStatusOnBidAccepted(event.offerId());
            inboxAdapter.markEventSuccess(event.getEventId(), CONSUMER);
            outboxPersistencePort.markProcessedByEventId(event.getEventId());

        } catch (RuntimeException e) {
            log.error("ERROR: Offer status update failed for offer {} when bid {} was accepted.",
                    event.offerId(), event.bidId(), e);
            inboxAdapter.markEventFailure(event.getEventId(), CONSUMER, e.getMessage());
            failureLog.log(event.getClass().getSimpleName(), event.toString(), e);
        }
    }
}
