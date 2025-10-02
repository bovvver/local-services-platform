package com.github.bovvver.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
