package com.project.hexagonal.offer.application.dto.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record CreateOfferCommand(String title,
                                 String description,
                                 Instant endDate,
                                 BigDecimal totalPrice) {
}