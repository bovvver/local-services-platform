package com.github.bovvver.offermanagment;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DomainEventPublisherImpl implements DomainEventPublisher {

    private final ApplicationEventPublisher innerPublisher;

    @Override
    public void publish(final DomainEvent event) {
        innerPublisher.publishEvent(event);
    }
}
