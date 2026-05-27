package com.project.hexagonal.offer.infrastructure.adapter;

import com.project.hexagonal.offer.application.contract.output.persistence.OfferPersistencePort;
import com.project.hexagonal.offer.core.exception.OfferDomainException;
import com.project.hexagonal.offer.core.model.Offer;
import com.project.hexagonal.offer.infrastructure.entity.OfferEntity;
import com.project.hexagonal.offer.infrastructure.mapper.OfferDataMapper;
import com.project.hexagonal.offer.infrastructure.repository.OfferJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfferPersistenceAdapter implements OfferPersistencePort {

    private final OfferJpaRepository repository;
    private final OfferDataMapper dataMapper;

    @Override
    public void save(Offer offer) {
        OfferEntity entity = dataMapper.toEntity(offer);
        repository.save(entity);
    }

    @Override
    public Offer findById(UUID offerId) {
        OfferEntity entity = repository.findById(offerId)
                .orElseThrow(() -> new OfferDomainException("Offer not found with id: " + offerId));
        return dataMapper.toDomain(entity);
    }
}
