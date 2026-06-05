package com.project.hexagonal.shared.core.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    Instant occurredAt();

    default int getVersion() {
        return 1;
    }

    default String getEventType() {
        return this.getClass().getSimpleName();
    }
}
