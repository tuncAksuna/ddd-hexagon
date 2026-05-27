package com.project.hexagonal.bid.application.dto.command;

import java.math.BigDecimal;
import java.util.UUID;

public record BidApplyCommand(BigDecimal totalPrice,
                              UUID bidderId,
                              UUID offerId) {
}