package com.project.hexagonal.bid.application.dto.query;

import java.util.UUID;

public record OfferSnapshot(UUID offerId, String status) {

    public boolean isPublished() {
        return "PUBLISHED".equals(status);
    }
}
