package com.project.hexagonal.offer.application.contract.output.persistence;

import com.project.hexagonal.offer.core.model.Offer;

import java.util.UUID;

public interface OfferPersistencePort {

    void save(Offer offer);

    Offer findById(UUID offerId);
}
