package com.project.hexagonal.shared.infrastructure.persistence.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hexagonal.shared.application.contract.dto.OutboxContractModel;
import com.project.hexagonal.shared.application.contract.output.OutboxPersistencePort;
import com.project.hexagonal.shared.core.events.DomainEvent;
import com.project.hexagonal.shared.infrastructure.persistence.entity.DeadLetterLogEntity;
import com.project.hexagonal.shared.infrastructure.persistence.entity.OutboxEntity;
import com.project.hexagonal.shared.infrastructure.persistence.enums.DeadLetterStatus;
import com.project.hexagonal.shared.infrastructure.persistence.enums.OutboxStatus;
import com.project.hexagonal.shared.infrastructure.persistence.mapper.OutboxDataMapper;
import com.project.hexagonal.shared.infrastructure.persistence.repository.DeadLetterLogJpaRepository;
import com.project.hexagonal.shared.infrastructure.persistence.repository.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPersistenceAdapter implements OutboxPersistencePort {

    private static final int MAX_RETRIES = 3;
    private static final int BATCH_SIZE = 50;

    private final OutboxJpaRepository outboxJpaRepository;
    private final DeadLetterLogJpaRepository dlqRepository;
    private final ObjectMapper objectMapper;
    private final OutboxDataMapper dataMapper;


    @Override
    public void save(String aggregateType, UUID aggregateId, DomainEvent event) {
        try {
            String eventPayload = objectMapper.writeValueAsString(event);
            OutboxEntity entity = buildEntity(aggregateType, aggregateId, event, eventPayload);
            outboxJpaRepository.save(entity);
            log.info("Event saved to Outbox with eventId: {}, with type: {}, with aggregate: {}",
                    event.getEventId(), event.getEventType(), aggregateType);
        } catch (Exception e) {
            log.error("Failed to save event to Outbox", e);
            throw new RuntimeException("Outbox persistence failed !", e);
        }
    }

    @Override
    public List<OutboxContractModel> findPendingForPublishing(int limit) {
        List<OutboxEntity> outboxEntityList = outboxJpaRepository.findPendingForPublishing();
        return outboxEntityList.stream()
                .map(dataMapper::toModel)
                .toList();
    }

    @Override
    public List<OutboxContractModel> findPublishedForProcessing(int limit) {
        List<OutboxEntity> outboxEntityList = outboxJpaRepository.findPublishedForProcessing();
        return outboxEntityList.stream()
                .map(dataMapper::toModel)
                .toList();
    }

    @Override
    public void markPublished(UUID outboxId) {
        outboxJpaRepository.findById(outboxId).ifPresent(entity -> {
            entity.setStatus(OutboxStatus.PUBLISHED);
            entity.setPublishedAt(Instant.now());
            outboxJpaRepository.save(entity);
            log.info("Event marked as PUBLISHED: {}", outboxId);
        });
    }

    @Override
    public void markProcessed(UUID outboxId) {
        outboxJpaRepository.findById(outboxId).ifPresent(entity -> {
            entity.setStatus(OutboxStatus.PROCESSED);
            entity.setProcessedAt(Instant.now());
            entity.setBatchProcessed(true);
            outboxJpaRepository.save(entity);
            log.info("Event marked as PROCESSED: {}", outboxId);
        });
    }

    @Override
    public void markProcessedByEventId(UUID eventId) {
        outboxJpaRepository.findByEventId(eventId).ifPresent(entity -> markProcessed(entity.getId()));
    }

    @Override
    public void markFailed(UUID outboxId, String errorMessage) {
        outboxJpaRepository.findById(outboxId).ifPresent(entity -> {
            entity.setLastError(errorMessage);
            outboxJpaRepository.save(entity);
            log.warn("Event marked as FAILED: {} — {}", outboxId, errorMessage);
        });
    }

    @Override
    public void moveToDLQ(UUID outboxId, String errorMessage) {
        outboxJpaRepository.findById(outboxId).ifPresent(entity -> {
            entity.setStatus(OutboxStatus.FAILED);
            entity.setLastError(errorMessage);
            DeadLetterLogEntity dlq = buildDlq(outboxId, errorMessage, entity);
            dlqRepository.save(dlq);
            entity.setDeadLetterLogId(dlq.getId());
            outboxJpaRepository.save(entity);
            log.error("Event moved to DLQ: outboxId={}, dlqId={}", outboxId, dlq.getId());
        });
    }

    @Override
    public void updateRetryCount(UUID outboxId) {
        outboxJpaRepository.findById(outboxId).ifPresent(entity -> {
            entity.setRetryCount(entity.getRetryCount() + 1);
            outboxJpaRepository.save(entity);
        });
    }

    private OutboxEntity buildEntity(String aggregateType, UUID aggregateId, DomainEvent event, String eventPayload) {
        return OutboxEntity.builder()
                .id(UUID.randomUUID())
                .eventId(event.getEventId())
                .eventType(event.getEventType())
                .eventVersion(event.getVersion())
                .eventPayload(eventPayload)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .status(OutboxStatus.PENDING)
                .retryCount(0)
                .maxRetries(MAX_RETRIES)
                .batchProcessed(false)
                .createdAt(Instant.now())
                .build();
    }

    private static DeadLetterLogEntity buildDlq(UUID outboxId, String errorMessage, OutboxEntity entity) {
        return DeadLetterLogEntity.builder()
                .outboxId(outboxId)
                .eventType(entity.getEventType())
                .eventPayload(entity.getEventPayload())
                .lastError(errorMessage)
                .failureCount(entity.getRetryCount())
                .status(DeadLetterStatus.PENDING)
                .createdAt(Instant.now())
                .build();
    }
}
