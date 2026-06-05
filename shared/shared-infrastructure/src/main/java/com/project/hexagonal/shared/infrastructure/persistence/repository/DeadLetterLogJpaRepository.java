package com.project.hexagonal.shared.infrastructure.persistence.repository;

import com.project.hexagonal.shared.infrastructure.persistence.entity.DeadLetterLogEntity;
import com.project.hexagonal.shared.infrastructure.persistence.enums.DeadLetterStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeadLetterLogJpaRepository extends JpaRepository<DeadLetterLogEntity, UUID> {

    Optional<DeadLetterLogEntity> findByOutboxId(UUID outboxId);

    List<DeadLetterLogEntity> findByStatus(DeadLetterStatus status);

    List<DeadLetterLogEntity> findByStatusOrderByCreatedAtDesc(DeadLetterStatus status);
}
