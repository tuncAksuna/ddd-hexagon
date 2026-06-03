package com.project.hexagonal.bid.infrastructure.adapter;

import com.project.hexagonal.bid.application.contract.output.query.OfferProjectionPort;
import com.project.hexagonal.bid.application.dto.query.OfferSnapshot;
import com.project.hexagonal.bid.infrastructure.entity.OfferProjectionEntity;
import com.project.hexagonal.bid.infrastructure.mapper.BidDataMapper;
import com.project.hexagonal.bid.infrastructure.repository.OfferProjectionJpaRepository;
import com.project.hexagonal.shared.core.exception.SourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OfferProjectioPersistenceAdapter implements OfferProjectionPort {

    private final OfferProjectionJpaRepository repository;
    private final BidDataMapper dataMapper;

    @Override
    public OfferSnapshot findById(UUID offerId) {
        Optional<OfferProjectionEntity> offerProjection = repository.findById(offerId);
        if (offerProjection.isEmpty()) {
            throw new SourceNotFoundException("Offer not found with id: " + offerId);
        }
        return dataMapper.toSnapshotFromProjection(offerProjection.get());
    }
}
