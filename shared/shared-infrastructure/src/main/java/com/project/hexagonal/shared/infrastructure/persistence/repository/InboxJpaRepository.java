package com.project.hexagonal.shared.infrastructure.persistence.repository;

import com.project.hexagonal.shared.infrastructure.persistence.entity.InboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface InboxJpaRepository extends JpaRepository<InboxEntity, UUID> {

    // Duplicate check
    Optional<InboxEntity> findByEventIdAndConsumerType(UUID eventId, String consumerType);

    void deleteByEventIdAndConsumerType(UUID eventId, String consumerType);

    // Cleanup: delete processed events older than a retention period and processed is true
    long deleteByProcessedTrueAndProcessedAtBefore(Instant before);
}

