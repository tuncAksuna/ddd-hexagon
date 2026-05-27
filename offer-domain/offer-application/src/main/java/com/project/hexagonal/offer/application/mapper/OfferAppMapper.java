package com.project.hexagonal.offer.application.mapper;

import com.project.hexagonal.offer.application.dto.command.CreateOfferCommand;
import com.project.hexagonal.offer.core.model.Offer;
import com.project.hexagonal.shared.application.annotation.AppMapper;
import com.project.hexagonal.shared.core.valueobject.Money;

@AppMapper
public class OfferAppMapper {

    public Offer toDomain(CreateOfferCommand command) {
        return Offer.builder()
                .title(command.title())
                .description(command.description())
                .endDate(command.endDate())
                .totalPrice(new Money(command.totalPrice()))
                .build();
    }
}