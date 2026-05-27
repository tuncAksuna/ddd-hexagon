package com.project.hexagonal.offer.api.mapper;

import com.project.hexagonal.offer.api.dto.CreateOfferRequest;
import com.project.hexagonal.offer.application.dto.command.CreateOfferCommand;
import org.springframework.stereotype.Component;

@Component
public class OfferApiMapper {

    public CreateOfferCommand toCreateCommand(CreateOfferRequest req) {
        return CreateOfferCommand.builder()
                .title(req.title())
                .description(req.description())
                .endDate(req.endDate())
                .totalPrice(req.totalPrice())
                .build();
    }
}