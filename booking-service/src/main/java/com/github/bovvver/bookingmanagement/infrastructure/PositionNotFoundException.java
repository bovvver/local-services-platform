package com.github.bovvver.bookingmanagement.infrastructure;

import java.util.UUID;

public class PositionNotFoundException extends RuntimeException {

    public PositionNotFoundException(UUID positionId) {
        super("Position not found: %s".formatted(positionId));
    }
}
