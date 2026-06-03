package com.project.hexagonal.shared.core.entity;

import com.project.hexagonal.shared.core.events.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AggregateRoot<ID> extends BaseEntity<ID> {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AggregateRoot<?> that = (AggregateRoot<?>) o;
        return Objects.equals(domainEvents, that.domainEvents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), domainEvents);
    }
}
