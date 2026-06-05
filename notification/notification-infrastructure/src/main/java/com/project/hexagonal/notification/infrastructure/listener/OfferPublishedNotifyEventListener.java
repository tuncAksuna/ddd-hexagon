package com.project.hexagonal.notification.infrastructure.listener;

import com.project.hexagonal.notification.application.contract.input.NotificationApplicationService;
import com.project.hexagonal.shared.application.contract.output.OutboxPersistencePort;
import com.project.hexagonal.shared.events.notification.OfferPublishedNotifyEvent;
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
public class OfferPublishedNotifyEventListener {

    private static final String CONSUMER = "notification";

    private final NotificationApplicationService notifyAppService;
    private final InboxPersistenceAdapter inboxAdapter;
    private final OutboxPersistencePort outboxPersistencePort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void handle(OfferPublishedNotifyEvent event) {
        try {
            if (inboxAdapter.isEventAlreadyProcessed(event.getEventId(), CONSUMER)) {
                log.info("Event already processed (skipping duplicate): eventId={}", event.getEventId());
                return;
            }

            inboxAdapter.markEventStart(event.getEventId(), event.getEventType(), CONSUMER);
            log.info("Sending notification for offer: {} at : {}", event.offerId(), event.occurredAt());
            notifyAppService.notify("Offer with id %s has been published at %s", event.offerId(), event.occurredAt());
            inboxAdapter.markEventSuccess(event.getEventId(), CONSUMER);
            outboxPersistencePort.markProcessedByEventId(event.getEventId());

        } catch (RuntimeException e) {
            log.error("CRITICAL: Notification failed for offer {}. Manual review may be needed.", event.offerId(), e);
            inboxAdapter.markEventFailure(event.getEventId(), CONSUMER, e.getMessage());
        }
    }
}
