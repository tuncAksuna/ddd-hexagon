package com.project.hexagonal.offer.core.model;

import com.project.hexagonal.offer.core.exception.OfferDomainException;
import com.project.hexagonal.offer.core.valueobject.OfferId;
import com.project.hexagonal.offer.core.valueobject.OfferStatus;
import com.project.hexagonal.shared.core.entity.AggregateRoot;
import com.project.hexagonal.shared.core.exception.DomainException;
import com.project.hexagonal.shared.core.valueobject.Money;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Offer extends AggregateRoot<OfferId> {

    private String title;
    private String description;
    private String code;
    private Instant startDate;
    private Instant endDate;
    private OfferStatus status;

    private Money totalPrice;

    protected void initialize() {
        validate();
        super.setId(new OfferId(UUID.randomUUID()));
        startDate = Instant.now();
        code = generateOfferCode();
        status = OfferStatus.DRAFT;
    }

    private void validate() {
        validateInitial();
        validatetotalPrice();
        validateDescription();
        validateEndDate();
        validateTitle();
        validateCode();
    }

    protected void changeDescription(String newDescription) {
        validateDescription();
        description = newDescription;
    }

    protected void changeEndDate(Instant newEndDate) {
        validateEndDate();
        endDate = newEndDate;
    }

    protected void changeTitle(String newTitle) {
        validateTitle();
        title = newTitle;
    }

    protected void processStatus() {
        if (Objects.nonNull(status)) {
            if (this.status.equals(OfferStatus.DRAFT)) {
                status = OfferStatus.PUBLISHED;
            }
            if (status.equals(OfferStatus.PUBLISHED)) {
                status = OfferStatus.CLOSED;
            }
            if (status.equals(OfferStatus.UPDATED)) {
                status = OfferStatus.PUBLISHED;
            }
            if (status.equals(OfferStatus.CLOSED)) {
                throw new DomainException("Closed Offer cannot be proceed !");
            }
            if (status.equals(OfferStatus.CANCELLED)) {
                throw new DomainException("Cancelled Offer cannot be proceed !");
            }
        }
    }

    private void validateCode() {
        if (Objects.nonNull(code)) {
            throw new OfferDomainException("Offer code must be null! Code is generated automatically by system...");
        }
    }

    private void validateTitle() {
        if (Objects.isNull(title) || title.length() < 5) {
            throw new OfferDomainException("Offer title must be greater then 5 characters and must not be null!");
        }
    }

    private void validateEndDate() {
        if (Objects.nonNull(endDate) || endDate.isBefore(startDate) || endDate.isBefore(Instant.now())) {
            throw new OfferDomainException("End date must be greater then start date and must be greater then current date !");
        }
    }

    private void validateDescription() {
        if (Objects.nonNull(description) && description.length() > 510) {
            throw new OfferDomainException("Offer description must not exceed 510 characters !");
        }
    }

    private void validatetotalPrice() {
        if (Objects.isNull(totalPrice) || totalPrice.isLowerThenZero()) {
            throw new OfferDomainException("Total price must be greater then zero");
        }
    }

    private void validateInitial() {
        if (Objects.nonNull(startDate) || Objects.nonNull(status) && Objects.nonNull(super.getId())) {
            throw new OfferDomainException("Initial offer object could not have an Id or Status or Start Date !");
        }
    }

    private String generateOfferCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private Offer(Builder builder) {
        super.setId(builder.offerId);
        description = builder.description;
        code = builder.offerCode;
        startDate = builder.startDate;
        title = builder.title;
        endDate = builder.endDate;
        status = builder.status;
        totalPrice = builder.totalPrice;
    }


    public static final class Builder {
        private OfferId offerId;
        private String description;
        private String offerCode;
        private Instant startDate;
        private String title;
        private Instant endDate;
        private OfferStatus status;
        private Money totalPrice;

        public Builder() {
        }

        public Builder offerId(OfferId val) {
            offerId = val;
            return this;
        }

        public Builder description(String val) {
            description = val;
            return this;
        }

        public Builder offerCode(String val) {
            offerCode = val;
            return this;
        }

        public Builder startDate(Instant val) {
            startDate = val;
            return this;
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder endDate(Instant val) {
            endDate = val;
            return this;
        }

        public Builder status(OfferStatus val) {
            status = val;
            return this;
        }

        public Builder totalPrice(Money val) {
            totalPrice = val;
            return this;
        }

        public Offer build() {
            return new Offer(this);
        }
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }
}
