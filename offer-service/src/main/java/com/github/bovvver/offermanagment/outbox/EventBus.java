package com.github.bovvver.offermanagment.outbox;

public interface EventBus {
    void publish(OutboxEvent outboxEvent);
}
