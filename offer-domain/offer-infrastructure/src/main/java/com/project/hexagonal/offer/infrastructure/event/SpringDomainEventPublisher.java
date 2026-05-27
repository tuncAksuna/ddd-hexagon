package com.project.hexagonal.offer.infrastructure.event;

import com.project.hexagonal.shared.application.event.DomainEventPublisher;
import com.project.hexagonal.shared.core.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publishEvent(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
