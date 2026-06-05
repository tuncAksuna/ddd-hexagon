package com.project.hexagonal.shared.infrastructure.persistence.enums;

public enum OutboxStatus {
    // not published yet
    PENDING,
    PUBLISHED,
    PROCESSED,
    // max retry count reached, DLQ
    FAILED
}
