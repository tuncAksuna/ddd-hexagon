package com.project.hexagonal.bid.application;

import com.project.hexagonal.bid.application.contract.input.BidApplicationService;
import com.project.hexagonal.bid.application.contract.output.BidPersistencePort;
import com.project.hexagonal.bid.application.contract.output.OfferProjectionPort;
import com.project.hexagonal.bid.application.dto.command.BidApplyCommand;
import com.project.hexagonal.bid.application.dto.query.OfferSnapshot;
import com.project.hexagonal.bid.application.mapper.BidAppMapper;
import com.project.hexagonal.bid.core.exception.BidDomainException;
import com.project.hexagonal.bid.core.model.Bid;
import com.project.hexagonal.bid.core.valueobject.BidStatus;
import com.project.hexagonal.shared.application.annotation.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@DomainService
@RequiredArgsConstructor
public class BidApplicationServiceImpl implements BidApplicationService {

    private final BidPersistencePort persistencePort;
    private final OfferProjectionPort offerProjectionPort;
    private final BidAppMapper appMapper;

    @Override
    @Transactional
    public void applyBid(BidApplyCommand command) {
        OfferSnapshot offer = offerProjectionPort.findById(command.offerId());
        if (!offer.isPublished()) {
            throw new BidDomainException("Cannot apply bid to an offer that is not in PUBLISHED state!");
        }
        Bid domainEntity = appMapper.toDomain(command);
        domainEntity.validateAndInitialize();
        persistencePort.save(domainEntity);
    }

    @Override
    @Transactional
    public void cancelBidsForOffer(UUID offerId) {
        List<Bid> bids = persistencePort.findByOfferId(offerId).stream()
                .filter(bid -> !BidStatus.CANCELLED.equals(bid.getStatus()))
                .toList();
        bids.forEach(Bid::cancel);
        persistencePort.saveAll(bids);
    }
}
