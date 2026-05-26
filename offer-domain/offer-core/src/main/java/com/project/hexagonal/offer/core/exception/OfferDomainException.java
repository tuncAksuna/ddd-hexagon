package com.project.hexagonal.offer.core.exception;

import com.project.hexagonal.shared.core.exception.DomainException;

public class OfferDomainException extends DomainException {
    public OfferDomainException(String message) {
        super(message);
    }
}
