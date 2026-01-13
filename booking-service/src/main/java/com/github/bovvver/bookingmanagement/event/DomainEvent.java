package com.github.bovvver.bookingmanagement.event;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime getTimestamp();
}
