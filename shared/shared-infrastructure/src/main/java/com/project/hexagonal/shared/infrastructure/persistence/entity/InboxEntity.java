package com.project.hexagonal.shared.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbox", indexes = {
        @Index(name = "idx_inbox_event_id", columnList = "event_id"),
        @Index(name = "idx_inbox_event_consumer", columnList = "event_id, consumer_type", unique = true),
        @Index(name = "idx_inbox_processed_at", columnList = "processed_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * idempotency key (UNIQUE per consumer)
     */
    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, length = 50)
    private String consumerType;

    @Column(nullable = false)
    private boolean processed;

    // Error handling
    @Column(columnDefinition = "TEXT")
    private String processingError;

    @Column(nullable = false)
    private int processingAttempts;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant processedAt;
}
