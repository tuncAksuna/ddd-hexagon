package com.project.hexagonal.shared.events.offer;

import com.project.hexagonal.shared.core.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OfferPublishedEvent(UUID eventId,
                                  UUID offerId,
                                  Instant occurredAt)
        implements DomainEvent {

    @Override
    public UUID getEventId() {
        return null;
    }
}
