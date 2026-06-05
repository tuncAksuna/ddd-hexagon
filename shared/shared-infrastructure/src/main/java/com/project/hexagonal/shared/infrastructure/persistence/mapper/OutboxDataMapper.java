package com.project.hexagonal.shared.infrastructure.persistence.mapper;

import com.project.hexagonal.shared.application.contract.dto.OutboxContractModel;
import com.project.hexagonal.shared.infrastructure.persistence.entity.OutboxEntity;
import org.springframework.stereotype.Component;

@Component
public class OutboxDataMapper {

    public OutboxContractModel toModel(OutboxEntity entity) {
        return OutboxContractModel.builder()
                .id(entity.getId())
                .eventId(entity.getEventId())
                .eventType(entity.getEventType())
                .eventVersion(entity.getEventVersion())
                .eventPayload(entity.getEventPayload())
                .aggregateType(entity.getAggregateType())
                .aggregateId(entity.getAggregateId())
                .status(entity.getStatus().name())
                .retryCount(entity.getRetryCount())
                .maxRetries(entity.getMaxRetries())
                .lastError(entity.getLastError())
                .createdAt(entity.getCreatedAt())
                .publishedAt(entity.getPublishedAt())
                .processedAt(entity.getProcessedAt())
                .deadLetterLogId(entity.getDeadLetterLogId())
                .batchProcessed(entity.isBatchProcessed())
                .build();

    }
}
