package com.project.hexagonal.shared.infrastructure.persistence.adapter;

import com.project.hexagonal.shared.infrastructure.persistence.entity.InboxEntity;
import com.project.hexagonal.shared.infrastructure.persistence.repository.InboxJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InboxPersistenceAdapter {

    private static final int MAX_LISTENER_RETRIES = 3;

    private final InboxJpaRepository repository;

    /**
     * Duplicate check.
     */
    @Transactional
    public boolean isEventAlreadyProcessed(UUID eventId, String consumerType) {
        return repository.findByEventIdAndConsumerType(eventId, consumerType)
                .map(InboxEntity::isProcessed)
                .orElse(false);
    }

    @Transactional
    public void markEventStart(UUID eventId, String eventType, String consumerType) {
        Optional<InboxEntity> existing = repository.findByEventIdAndConsumerType(eventId, consumerType);

        InboxEntity inbox;
        if (existing.isPresent()) {
            inbox = existing.get();
            if (inbox.isProcessed()) {
                log.info("Event already processed (duplicate): eventId={}, consumer={}", eventId, consumerType);
                return;
            }
            inbox.setProcessingAttempts(inbox.getProcessingAttempts() + 1);
        } else {
            inbox = InboxEntity.builder()
                    .eventId(eventId)
                    .eventType(eventType)
                    .consumerType(consumerType)
                    .processed(false)
                    .processingAttempts(1)
                    .createdAt(Instant.now())
                    .build();
        }
        repository.save(inbox);
    }

    // Mark as processed after successful processing
    @Transactional
    public void markEventSuccess(UUID eventId, String consumerType) {
        repository.findByEventIdAndConsumerType(eventId, consumerType)
                .ifPresent(inbox -> {
                    inbox.setProcessed(true);
                    inbox.setProcessedAt(Instant.now());
                    inbox.setProcessingError(null);
                    repository.save(inbox);
                    log.info("Event processed successfully: eventId={}, consumer={}", eventId, consumerType);
                });

    }

    // Mark processing failure
    @Transactional
    public void markEventFailure(UUID eventId, String consumerType, String error) {
        repository.findByEventIdAndConsumerType(eventId, consumerType)
                .ifPresent(inbox -> {
                    if (inbox.getProcessingAttempts() > MAX_LISTENER_RETRIES) {
                        inbox.setProcessingError(error);
                        inbox.setProcessed(true);
                        inbox.setProcessedAt(Instant.now());
                        log.warn("Event exhausted local retries with Event Id: {}", eventId);
                    }
                    repository.save(inbox);
                    log.warn("Event processing failed: eventId={}, consumer={}, error={}",
                            eventId, consumerType, error);
                });
    }
}
