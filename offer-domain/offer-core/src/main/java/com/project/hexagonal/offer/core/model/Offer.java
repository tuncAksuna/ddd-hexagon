package com.project.hexagonal.offer.core.model;

import com.project.hexagonal.offer.core.exception.OfferDomainException;
import com.project.hexagonal.offer.core.valueobject.OfferId;
import com.project.hexagonal.offer.core.valueobject.OfferStatus;
import com.project.hexagonal.shared.core.entity.AggregateRoot;
import com.project.hexagonal.shared.core.valueobject.Money;
import com.project.hexagonal.shared.events.offer.OfferClosedEvent;
import com.project.hexagonal.shared.events.offer.OfferPublishedEvent;
import com.project.hexagonal.shared.events.notification.OfferPublishedNotifyEvent;
import com.project.hexagonal.shared.events.offer.OfferCancelledEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Getter
@Builder
public class Offer extends AggregateRoot<OfferId> {

    private String title;
    private String description;
    private String code;
    private Instant startDate;
    private Instant endDate;
    private OfferStatus status;
    private Money totalPrice;

    public void validateAndInitialize() {
        validate();
        super.setId(new OfferId(UUID.randomUUID()));
        startDate = Instant.now();
        code = generateOfferCode();
        status = OfferStatus.DRAFT;
    }

    private void validate() {
        validateInitial();
        validateTotalPrice();
        validateDescription();
        validateStartDate();
        validateEndDate();
        validateTitle();
        validateCode();
    }

    public void changeDescription(String newDescription) {
        validateDescription();
        description = newDescription;
    }

    public void changeEndDate(Instant newEndDate) {
        validateEndDate();
        endDate = newEndDate;
    }

    public void changeTitle(String newTitle) {
        validateTitle();
        title = newTitle;
    }

    public void cancel() {
        if (OfferStatus.CANCELLED.equals(status)) return;
        status = status.cancel();
        registerEvent(new OfferCancelledEvent(super.getId().getVal(), Instant.now()));
    }

    public void close() {
        if (OfferStatus.CLOSED.equals(status)) return;
        if (!(OfferStatus.PUBLISHED.equals(status) || OfferStatus.UPDATED.equals(status))) {
            throw new OfferDomainException("Cannot close an offer that is not published or updated!");
        }
        status = OfferStatus.CLOSED;
        registerEvent(new OfferClosedEvent(super.getId().getVal(), Instant.now()));
    }

    public void processStatus() {
        if (Objects.isNull(status)) {
            throw new OfferDomainException("Cannot process an offer without a status !");
        }
        status = status.proceed();
        if (OfferStatus.PUBLISHED.equals(status)) {
            registerEvent(new OfferPublishedEvent(super.getId().getVal(), Instant.now()));
            registerEvent(new OfferPublishedNotifyEvent(super.getId().getVal(), Instant.now()));
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

    private void validateStartDate() {
        if (Objects.isNull(startDate) || startDate.isBefore(Instant.now())) {
            throw new OfferDomainException("Start date must be greater then current date and must not be null!!");
        }
    }

    private void validateEndDate() {
        if (Objects.isNull(endDate) || endDate.isBefore(startDate) || endDate.isBefore(Instant.now())) {
            throw new OfferDomainException("End date must be greater then start date and must be greater then current date !");
        }
    }

    private void validateDescription() {
        if (Objects.nonNull(description) && description.length() > 510) {
            throw new OfferDomainException("Offer description must not exceed 510 characters !");
        }
    }

    private void validateTotalPrice() {
        if (Objects.isNull(totalPrice) || totalPrice.isLowerThenZero()) {
            throw new OfferDomainException("Total price must be greater then zero");
        }
    }

    private void validateInitial() {
        if (Objects.nonNull(status) || Objects.nonNull(super.getId())) {
            throw new OfferDomainException("Initial offer object could not have an Id or Status!");
        }
    }

    private String generateOfferCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

}
