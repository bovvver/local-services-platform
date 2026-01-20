package com.github.bovvver.bookingmanagement.outbox;

public interface EventBus {
    void publish(OutboxEvent outboxEvent);
}
