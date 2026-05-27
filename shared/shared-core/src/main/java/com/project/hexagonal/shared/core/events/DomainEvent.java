package com.project.hexagonal.shared.core.events;

import java.time.Instant;

public interface DomainEvent {

    Instant occurredAt();
}