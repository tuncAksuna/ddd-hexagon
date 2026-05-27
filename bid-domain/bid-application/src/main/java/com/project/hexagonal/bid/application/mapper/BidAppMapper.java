package com.project.hexagonal.bid.application.mapper;

import com.project.hexagonal.bid.application.dto.command.BidApplyCommand;
import com.project.hexagonal.bid.core.model.Bid;
import com.project.hexagonal.shared.application.annotation.AppMapper;
import com.project.hexagonal.shared.core.valueobject.Money;

@AppMapper
public class BidAppMapper {

    public Bid toDomain(BidApplyCommand command) {
        return Bid.builder()
                .offerId(command.offerId())
                .bidderId(command.bidderId())
                .totalPrice(new Money(command.totalPrice()))
                .build();
    }
}
