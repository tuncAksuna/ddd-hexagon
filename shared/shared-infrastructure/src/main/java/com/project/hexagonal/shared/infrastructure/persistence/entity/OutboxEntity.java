package com.project.hexagonal.shared.infrastructure.persistence.entity;

import com.project.hexagonal.shared.infrastructure.persistence.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox", indexes = {
        @Index(name = "idx_outbox_status", columnList = "status"),
        @Index(name = "idx_outbox_created_at", columnList = "created_at"),
        @Index(name = "idx_outbox_retry_count", columnList = "retry_count")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEntity {

    @Id
    @Column(name = "OUTBOX_ID",
            columnDefinition = "UUID",
            nullable = false,
            updatable = false)
    private UUID id;

    /**
     * IDEMPOTENCY KEY
     */
    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private int eventVersion;

    @Column(columnDefinition = "TEXT",
            nullable = false)
    private String eventPayload;

    /**
     * Which domain ?
     */
    @Column(nullable = false, length = 50)
    private String aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private int retryCount;

    /**
     * Default 3
     */
    @Column(nullable = false)
    private int maxRetries;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant publishedAt;

    private Instant processedAt;

    private UUID deadLetterLogId;

    @Column(nullable = false)
    private boolean batchProcessed;

}
