package com.project.hexagonal.shared.infrastructure.persistence.repository;

import com.project.hexagonal.shared.infrastructure.persistence.entity.OutboxEntity;
import com.project.hexagonal.shared.infrastructure.persistence.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity, UUID> {

    @Query("SELECT o FROM OutboxEntity o WHERE o.status = 'PENDING' " +
            "AND o.retryCount < o.maxRetries ORDER BY o.createdAt ASC")
    List<OutboxEntity> findPendingForPublishing();

    @Query("SELECT o FROM OutboxEntity o WHERE o.status = 'PUBLISHED' " +
            "AND o.batchProcessed = false ORDER BY o.publishedAt ASC")
    List<OutboxEntity> findPublishedForProcessing();

    List<OutboxEntity> findByStatusAndCreatedAtBefore(OutboxStatus status, Instant before);

    boolean existsByEventId(UUID eventId);

    // Find by event ID (used by listeners to mark processed)
    Optional<OutboxEntity> findByEventId(UUID eventId);
}
