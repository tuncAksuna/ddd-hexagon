package com.project.hexagonal.shared.infrastructure.persistence.entity;

import com.project.hexagonal.shared.infrastructure.persistence.enums.EventFailureStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_failure_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFailureLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String eventData;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventFailureStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
}
