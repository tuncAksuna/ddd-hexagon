package com.project.hexagonal.shared.infrastructure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hexagonal.shared.application.contract.dto.OutboxContractModel;
import com.project.hexagonal.shared.application.contract.output.OutboxPersistencePort;
import com.project.hexagonal.shared.core.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxPersistencePort outboxPort;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    private static final int BATCH_SIZE = 50;
    private static final int MAX_RETRIES = 3;

    // 2 SECONDS
    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishPendingEvents() {
        try {
            List<OutboxContractModel> pendingEvents = outboxPort.findPendingForPublishing(BATCH_SIZE);

            if (pendingEvents.isEmpty()) {
                return;
            }

            log.info("Publishing batch of {} pending events", pendingEvents.size());

            for (OutboxContractModel outbox : pendingEvents) {
                publishSingleEvent(outbox);
            }
        } catch (Exception e) {
            log.error("Outbox publishing scheduler error", e);
        }
    }

    private void publishSingleEvent(OutboxContractModel outbox) {
        try {
            Class<?> eventClass = resolveEventClass(outbox.eventType());
            DomainEvent event = (DomainEvent) objectMapper.readValue(outbox.eventPayload(), eventClass);

            eventPublisher.publishEvent(event);
            outboxPort.markPublished(outbox.id());
            log.info("Event published: eventId={}, type={}", event.getEventId(), outbox.eventType());

        } catch (Exception e) {
            log.error("Failed to publish event: {}", outbox.id(), e);
            handlePublishingFailure(outbox, e);
        }
    }

    private void handlePublishingFailure(OutboxContractModel outbox, Exception e) {
        outboxPort.updateRetryCount(outbox.id());
        outboxPort.markFailed(outbox.id(), e.getMessage());

        // If max retries exceeded → DLQ
        if (outbox.retryCount() >= MAX_RETRIES) {
            outboxPort.moveToDLQ(outbox.id(),
                    "Max retries exceeded after " + MAX_RETRIES + " attempts: " + e.getMessage());
            log.warn("Event moved to DLQ after {} retries: {}", MAX_RETRIES, outbox.id());
        }
    }

    private Class<?> resolveEventClass(String eventType) throws ClassNotFoundException {
        String[] possiblePackages = {
                "com.project.hexagonal.shared.events.offer.",
                "com.project.hexagonal.shared.events.bid.",
                "com.project.hexagonal.shared.events.notification."
        };

        for (String pkg : possiblePackages) {
            try {
                return Class.forName(pkg + eventType);
            } catch (ClassNotFoundException e) {
            }
        }

        throw new ClassNotFoundException("Event class not found: " + eventType);
    }

}
