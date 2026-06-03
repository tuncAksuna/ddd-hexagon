package com.project.hexagonal.shared.events.bid;

import com.project.hexagonal.shared.core.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record BidAcceptedEvent(UUID bidId, UUID offerId, Instant occurredAt) implements DomainEvent {
}
