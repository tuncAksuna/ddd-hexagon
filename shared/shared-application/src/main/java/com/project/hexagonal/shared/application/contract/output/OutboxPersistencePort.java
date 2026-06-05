package com.project.hexagonal.shared.application.contract.output;

import com.project.hexagonal.shared.application.contract.dto.OutboxContractModel;
import com.project.hexagonal.shared.core.events.DomainEvent;

import java.util.List;
import java.util.UUID;

public interface OutboxPersistencePort {

    void save(String aggregateType, UUID aggregateId, DomainEvent event);

    List<OutboxContractModel> findPendingForPublishing(int limit);

    List<OutboxContractModel> findPublishedForProcessing(int limit);

    void markPublished(UUID outboxId);

    void markProcessed(UUID outboxId);

    // Mark processed by event ID (used by listeners after processing)
    void markProcessedByEventId(UUID eventId);

    void markFailed(UUID outboxId, String errorMessage);

    void moveToDLQ(UUID outboxId, String errorMessage);

    void updateRetryCount(UUID outboxId);

}
