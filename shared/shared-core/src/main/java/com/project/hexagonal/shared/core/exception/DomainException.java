package com.project.hexagonal.shared.core.exception;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
