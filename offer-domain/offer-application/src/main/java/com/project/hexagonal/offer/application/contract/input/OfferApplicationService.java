package com.project.hexagonal.offer.application.contract.input;

import com.project.hexagonal.offer.application.dto.command.CreateOfferCommand;

import java.util.UUID;

public interface OfferApplicationService {

    void create(CreateOfferCommand command);

    void publish(UUID offerId);

    void cancel(UUID offerId);

    void updateStatusOnBidAccepted(UUID offerId);
}
