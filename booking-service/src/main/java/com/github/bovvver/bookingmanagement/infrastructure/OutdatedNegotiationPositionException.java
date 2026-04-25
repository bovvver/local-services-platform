package com.github.bovvver.bookingmanagement.infrastructure;

import java.util.UUID;

public class OutdatedNegotiationPositionException extends RuntimeException {

    public OutdatedNegotiationPositionException(UUID positionId) {
        super("Position with id %s is outdated. Please check latest position.".formatted(positionId));
    }
}
