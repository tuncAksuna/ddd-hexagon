package com.project.hexagonal.shared.infrastructure.exception;

public class EventListenerException extends RuntimeException {

    public EventListenerException(String message) {
        super(message);
    }

    public EventListenerException(String message, Throwable cause) {
        super(message, cause);
    }
}
