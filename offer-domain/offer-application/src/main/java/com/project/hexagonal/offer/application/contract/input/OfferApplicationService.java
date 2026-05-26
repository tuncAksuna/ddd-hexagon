package com.project.hexagonal.offer.application.contract.input;

import com.project.hexagonal.offer.application.dto.command.CreateOfferCommand;

public interface OfferApplicationService {

    void create(CreateOfferCommand command);
}
