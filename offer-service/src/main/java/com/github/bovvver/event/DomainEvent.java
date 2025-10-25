package com.github.bovvver.event;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime getTimestamp();
}
