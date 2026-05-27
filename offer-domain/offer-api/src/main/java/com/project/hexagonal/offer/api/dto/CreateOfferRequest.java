package com.project.hexagonal.offer.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateOfferRequest(
        @NotBlank String title,
        String description,
        @NotNull @Future Instant endDate,
        @NotNull @Positive BigDecimal totalPrice
) {
}