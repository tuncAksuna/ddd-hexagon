package com.project.hexagonal.bid.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record BidApplyRequest(
        @NotNull UUID bidderId,
        @NotNull @Positive BigDecimal totalPrice
) {
}