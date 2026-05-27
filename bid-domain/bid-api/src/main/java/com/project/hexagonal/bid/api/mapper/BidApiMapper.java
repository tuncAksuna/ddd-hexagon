package com.project.hexagonal.bid.api.mapper;

import com.project.hexagonal.bid.api.dto.BidApplyRequest;
import com.project.hexagonal.bid.application.dto.command.BidApplyCommand;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BidApiMapper {

    public BidApplyCommand toCommand(BidApplyRequest request, UUID offerId) {
        return new BidApplyCommand(request.totalPrice(), request.bidderId(), offerId);
    }
}
