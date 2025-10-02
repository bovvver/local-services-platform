package com.github.bovvver.event;

import java.time.Instant;

public interface DomainEvent {
    Instant getTimestamp();
}
