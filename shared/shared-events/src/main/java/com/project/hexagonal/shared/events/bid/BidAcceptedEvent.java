package com.project.hexagonal.shared.events.bid;

import com.project.hexagonal.shared.core.events.DomainEvent;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

public record BidAcceptedEvent
        (UUID eventId,
         UUID bidId,
         UUID offerId,
         Instant occurredAt)
        implements DomainEvent {

    @Override
    public UUID getEventId() {
        return eventId;
    }

}
