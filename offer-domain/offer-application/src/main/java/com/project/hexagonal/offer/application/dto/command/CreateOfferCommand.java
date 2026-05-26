package com.project.hexagonal.offer.application.dto.command;

import com.project.hexagonal.offer.core.valueobject.OfferStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record CreateOfferCommand(String title,
                                 String description,
                                 Instant startDate,
                                 Instant endDate,
                                 OfferStatus status,
                                 BigDecimal totalPrice) {
}
