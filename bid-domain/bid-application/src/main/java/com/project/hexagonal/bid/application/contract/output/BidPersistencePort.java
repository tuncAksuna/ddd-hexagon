package com.project.hexagonal.bid.application.contract.output;

import com.project.hexagonal.bid.core.model.Bid;

import java.util.List;
import java.util.UUID;

public interface BidPersistencePort {

    void save(Bid bid);

    void saveAll(List<Bid> bids);

    List<Bid> findByOfferId(UUID offerId);
}
