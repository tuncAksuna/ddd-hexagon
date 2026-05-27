package com.project.hexagonal.bid.core.model;

import com.project.hexagonal.bid.core.exception.BidDomainException;
import com.project.hexagonal.bid.core.valueobject.BidId;
import com.project.hexagonal.bid.core.valueobject.BidStatus;
import com.project.hexagonal.shared.core.entity.AggregateRoot;
import com.project.hexagonal.shared.core.valueobject.Money;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
public class Bid extends AggregateRoot<BidId> {

    private UUID offerId;
    private UUID bidderId;
    private Money totalPrice;
    private BidStatus status;
    private Instant submittedAt;

    public void validateAndInitialize() {
        validate();
        super.setId(new BidId(UUID.randomUUID()));
        submittedAt = Instant.now();
        status = BidStatus.SUBMITTED;
    }

    private void validate() {
        validateInitial();
        validateOfferId();
        validateBidderId();
        validateBidAmount();
    }

    public void accept() {
        if (Objects.isNull(status)) {
            throw new BidDomainException("Cannot accept a bid without a status !");
        }
        status = status.accept();
    }

    public void cancel() {
        if (Objects.isNull(status)) {
            throw new BidDomainException("Cannot cancel a bid without a status !");
        }
        status = status.cancel();
    }

    private void validateInitial() {
        if (Objects.nonNull(submittedAt) || Objects.nonNull(status) || Objects.nonNull(super.getId())) {
            throw new BidDomainException("Initial bid object could not have an Id, Status or Submitted Date !");
        }
    }

    private void validateOfferId() {
        if (Objects.isNull(offerId)) {
            throw new BidDomainException("Bid must reference a valid offer !");
        }
    }

    private void validateBidderId() {
        if (Objects.isNull(bidderId)) {
            throw new BidDomainException("Bid must have a valid bidder !");
        }
    }

    private void validateBidAmount() {
        if (Objects.isNull(totalPrice) || !totalPrice.isGreaterThenZero()) {
            throw new BidDomainException("Bid total price must be greater than zero or must not be null!");
        }
    }

}
