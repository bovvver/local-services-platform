package com.github.bovvver.offermanagment.outbox;

import com.github.bovvver.offermanagment.events.DomainEvent;

public interface EventBus {
    void publish(DomainEvent domainEvent);
}
