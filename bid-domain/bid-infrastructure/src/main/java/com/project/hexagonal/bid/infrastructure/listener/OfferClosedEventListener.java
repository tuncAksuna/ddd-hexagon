package com.project.hexagonal.bid.infrastructure.listener;

import com.project.hexagonal.bid.application.contract.input.BidApplicationService;
import com.project.hexagonal.shared.application.contract.output.OutboxPersistencePort;
import com.project.hexagonal.shared.events.offer.OfferCancelledEvent;
import com.project.hexagonal.shared.events.offer.OfferClosedEvent;
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
public class OfferClosedEventListener {

    private static final String CONSUMER = "bid";

    private static final String ERROR_MSG = "CRITICAL: Bid cancel failed for offer {}. Event will be logged for manual intervention.";
    private static final String EVENT_ALREADY_PROCESSED_MSG = "Event with id {} already processed !";
    private static final String EVENT_MARK_SUCCES_MSG = "OfferCancelledEvent processed successfully: eventId={}";

    private final BidApplicationService bidApplicationService;
    private final InboxPersistenceAdapter inboxAdapter;
    private final EventFailureLogAdapter failureLog;
    private final OutboxPersistencePort outboxPersistencePort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClosed(OfferClosedEvent event) {
        try {
            // duplication check
            if (inboxAdapter.isEventAlreadyProcessed(event.getEventId(), CONSUMER)) {
                log.info(EVENT_ALREADY_PROCESSED_MSG, event.getEventId());
                return;
            }
            inboxAdapter.markEventStart(event.getEventId(), event.getEventType(), CONSUMER);

            log.info("Processing OfferClosedEvent: offerId={}", event.offerId());
            bidApplicationService.cancelBidsForOffer(event.offerId());

            inboxAdapter.markEventSuccess(event.getEventId(), CONSUMER);
            outboxPersistencePort.markProcessedByEventId(event.getEventId());
            log.info(EVENT_MARK_SUCCES_MSG, event.getEventId());
        } catch (RuntimeException e) {
            log.error(ERROR_MSG, event.offerId(), e);
            inboxAdapter.markEventFailure(event.getEventId(), CONSUMER, e.getMessage());
            failureLog.log(event.getClass().getSimpleName(), event.toString(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCancelled(OfferCancelledEvent event) {
        try {
            if (inboxAdapter.isEventAlreadyProcessed(event.getEventId(), CONSUMER)) {
                log.info(EVENT_ALREADY_PROCESSED_MSG, event.getEventId());
                return;
            }
            inboxAdapter.markEventStart(event.getEventId(), event.getEventType(), CONSUMER);

            log.info("Processing OfferCancelledEvent: offerId={}", event.offerId());
            bidApplicationService.cancelBidsForOffer(event.offerId());

            inboxAdapter.markEventSuccess(event.getEventId(), CONSUMER);
            outboxPersistencePort.markProcessedByEventId(event.getEventId());
            log.info(EVENT_MARK_SUCCES_MSG, event.getEventId());
        } catch (RuntimeException e) {
            log.error(ERROR_MSG, event.offerId(), e);
            inboxAdapter.markEventFailure(event.getEventId(), CONSUMER, e.getMessage());
            failureLog.log(event.getClass().getSimpleName(), event.toString(), e);
        }
    }
}
