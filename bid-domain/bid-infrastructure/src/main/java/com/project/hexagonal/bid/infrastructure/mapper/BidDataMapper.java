package com.project.hexagonal.bid.infrastructure.mapper;

import com.project.hexagonal.bid.application.dto.query.OfferSnapshot;
import com.project.hexagonal.bid.core.model.Bid;
import com.project.hexagonal.bid.core.valueobject.BidId;
import com.project.hexagonal.bid.infrastructure.entity.BidEntity;
import com.project.hexagonal.bid.infrastructure.entity.OfferProjectionEntity;
import com.project.hexagonal.shared.core.valueobject.Money;
import com.project.hexagonal.shared.infrastructure.annotation.DomainMapper;

import java.util.List;

@DomainMapper
public class BidDataMapper {

    public BidEntity toEntity(Bid domainEntity) {
        return BidEntity.builder()
                .id(domainEntity.getId().getVal())
                .offerId(domainEntity.getOfferId())
                .bidderId(domainEntity.getBidderId())
                .totalPrice(domainEntity.getTotalPrice().getAmount())
                .status(domainEntity.getStatus())
                .submittedAt(domainEntity.getSubmittedAt())
                .build();
    }

    public List<Bid> toDomainList(List<BidEntity> entityList) {
        return entityList.stream()
                .map(entity -> {
                    Bid bid = Bid.builder()
                            .offerId(entity.getOfferId())
                            .bidderId(entity.getBidderId())
                            .totalPrice(new Money(entity.getTotalPrice()))
                            .status(entity.getStatus())
                            .submittedAt(entity.getSubmittedAt())
                            .build();
                    bid.setId(new BidId(entity.getId()));
                    return bid;
                }).toList();
    }

    public OfferSnapshot toSnapshotFromProjection(OfferProjectionEntity entity) {
        return new OfferSnapshot(entity.getId(), entity.getStatus().name());
    }

}
