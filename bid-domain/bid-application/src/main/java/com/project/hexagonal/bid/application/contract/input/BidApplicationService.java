package com.project.hexagonal.bid.application.contract.input;

import com.project.hexagonal.bid.application.dto.command.BidApplyCommand;

import java.util.UUID;

public interface BidApplicationService {

    void applyBid(BidApplyCommand command);

    void cancelBidsForOffer(UUID offerId);
}
