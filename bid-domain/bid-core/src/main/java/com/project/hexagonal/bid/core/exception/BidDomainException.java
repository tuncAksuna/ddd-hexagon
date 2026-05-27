package com.project.hexagonal.bid.core.exception;

import com.project.hexagonal.shared.core.exception.DomainException;

public class BidDomainException extends DomainException {
    public BidDomainException(String message) {
        super(message);
    }
}
