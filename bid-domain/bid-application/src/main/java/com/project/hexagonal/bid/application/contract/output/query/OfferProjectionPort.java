package com.project.hexagonal.bid.application.contract.output.query;

import com.project.hexagonal.bid.application.dto.query.OfferSnapshot;

import java.util.UUID;

public interface OfferProjectionPort {

    OfferSnapshot findById(UUID offerId);

}
