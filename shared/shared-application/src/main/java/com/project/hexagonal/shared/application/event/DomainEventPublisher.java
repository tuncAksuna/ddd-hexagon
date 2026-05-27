package com.project.hexagonal.shared.application.event;

import com.project.hexagonal.shared.core.events.DomainEvent;

public interface DomainEventPublisher {

    void publishEvent(DomainEvent event);
}
