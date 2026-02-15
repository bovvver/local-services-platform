package com.github.bovvver.offermanagment.outbox;

import com.github.bovvver.offermanagment.events.IntegrationEvent;

public interface EventBus {
    void publish(IntegrationEvent integrationEvent);
}
