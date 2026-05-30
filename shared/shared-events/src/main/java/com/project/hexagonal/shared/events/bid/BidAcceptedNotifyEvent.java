package com.project.hexagonal.shared.events.bid;

import com.project.hexagonal.shared.core.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record BidAcceptedNotifyEvent(UUID bidId,
                                     String status,
                                     Instant occurredAt) implements DomainEvent {
}
