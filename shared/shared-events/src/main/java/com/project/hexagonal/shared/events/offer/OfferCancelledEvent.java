package com.project.hexagonal.shared.events.offer;

import com.project.hexagonal.shared.core.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OfferCancelledEvent(UUID offerId, Instant occurredAt) implements DomainEvent {
}