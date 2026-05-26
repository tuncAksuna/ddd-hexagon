package com.project.hexagonal.offer.api.dto;

import com.project.hexagonal.offer.core.valueobject.OfferStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateOfferRequest(@NotNull String title,
                                 @NotNull String description,
                                 @NotNull Instant startDate,
                                 @NotNull Instant endDate,
                                 @NotNull OfferStatus status,
                                 @NotNull BigDecimal totalPrice) {
}
