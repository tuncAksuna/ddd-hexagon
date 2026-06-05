package com.project.hexagonal.shared.infrastructure.persistence.entity;


import com.project.hexagonal.shared.infrastructure.persistence.enums.DeadLetterStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dead_letter_log", indexes = {
        @Index(name = "idx_dlq_outbox_id", columnList = "outbox_id"),
        @Index(name = "idx_dlq_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeadLetterLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID outboxId;

    @Column(nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String eventPayload;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String lastError;

    @Column(nullable = false)
    private int failureCount;

    // Status: PENDING (awaiting manual intervention)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeadLetterStatus status;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant resolvedAt;
}
