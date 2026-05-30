package com.project.hexagonal.notification.infrastructure.listener;

import com.project.hexagonal.notification.application.contract.input.NotificationApplicationService;
import com.project.hexagonal.shared.events.bid.BidAcceptedNotifyEvent;
import com.project.hexagonal.shared.infrastructure.exception.EventListenerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidAcceptedNotifyEventListener {

    private final NotificationApplicationService notifyAppService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BidAcceptedNotifyEvent event) {
        try {
            log.info("Sending notification for bid: {} at : {}", event.bidId(), event.occurredAt());
            notifyAppService.notify("Bid with id %s has been accepted", event.bidId());
        } catch (EventListenerException e) {
            log.error("Error while sending notification for bid: {} at {}", event.bidId(), event.occurredAt(), e);
        }
    }
}
