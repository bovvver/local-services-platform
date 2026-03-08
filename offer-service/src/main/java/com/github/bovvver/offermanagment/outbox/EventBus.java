package com.github.bovvver.offermanagment.outbox;

import com.github.bovvver.contracts.IntegrationEvent;

public interface EventBus {
    void publish(IntegrationEvent integrationEvent);
}
