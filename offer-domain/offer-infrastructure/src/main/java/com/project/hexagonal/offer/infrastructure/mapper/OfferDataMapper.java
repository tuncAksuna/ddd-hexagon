package com.project.hexagonal.offer.infrastructure.mapper;

import com.project.hexagonal.offer.core.model.Offer;
import com.project.hexagonal.offer.core.valueobject.OfferId;
import com.project.hexagonal.offer.infrastructure.entity.OfferEntity;
import com.project.hexagonal.shared.core.valueobject.Money;
import com.project.hexagonal.shared.infrastructure.annotation.DomainMapper;

@DomainMapper
public class OfferDataMapper {

    public OfferEntity toEntity(Offer domainEntity) {
        return OfferEntity.builder()
                .id(domainEntity.getId().getVal())
                .code(domainEntity.getCode())
                .title(domainEntity.getTitle())
                .description(domainEntity.getDescription())
                .startDate(domainEntity.getStartDate())
                .endDate(domainEntity.getEndDate())
                .status(domainEntity.getStatus())
                .totalPrice(domainEntity.getTotalPrice().getAmount())
                .build();
    }

    public Offer toDomain(OfferEntity entity) {
        Offer offer = Offer.builder()
                .code(entity.getCode())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .totalPrice(new Money(entity.getTotalPrice()))
                .build();
        offer.setId(new OfferId(entity.getId()));
        return offer;
    }
}
