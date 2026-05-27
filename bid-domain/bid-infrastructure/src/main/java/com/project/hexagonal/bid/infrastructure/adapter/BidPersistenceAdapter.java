package com.project.hexagonal.bid.infrastructure.adapter;

import com.project.hexagonal.bid.application.contract.output.BidPersistencePort;
import com.project.hexagonal.bid.core.model.Bid;
import com.project.hexagonal.bid.infrastructure.entity.BidEntity;
import com.project.hexagonal.bid.infrastructure.mapper.BidDataMapper;
import com.project.hexagonal.bid.infrastructure.repository.BidJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BidPersistenceAdapter implements BidPersistencePort {

    private final BidJpaRepository repository;
    private final BidDataMapper dataMapper;

    @Override
    public void save(Bid bid) {
        BidEntity entity = dataMapper.toEntity(bid);
        repository.save(entity);
    }

    @Override
    public void saveAll(List<Bid> bids) {
        repository.saveAll(bids.stream().map(dataMapper::toEntity).toList());
    }

    @Override
    public List<Bid> findByOfferId(UUID offerId) {
        List<BidEntity> entityList = repository.findByOfferId(offerId);
        return dataMapper.toDomainList(entityList);
    }
}
