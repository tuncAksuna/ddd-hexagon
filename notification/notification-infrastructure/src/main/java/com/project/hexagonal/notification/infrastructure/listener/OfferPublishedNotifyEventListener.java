package com.project.hexagonal.notification.infrastructure.listener;

import com.project.hexagonal.notification.application.contract.input.NotificationApplicationService;
import com.project.hexagonal.shared.events.notification.OfferPublishedNotifyEvent;
import com.project.hexagonal.shared.infrastructure.exception.EventListenerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfferPublishedNotifyEventListener {

    private final NotificationApplicationService notifyAppService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OfferPublishedNotifyEvent event) {
        try {
            log.info("Sending notification for offer: {} at : {}", event.offerId(), event.occurredAt());
            notifyAppService.notify("Offer with id %s has been published at %s", event.offerId(), event.occurredAt());
        } catch (EventListenerException e) {
            log.error("Error while sending notification for offer: {} at {}", event.offerId(), event.occurredAt(), e);
        }
    }
}
