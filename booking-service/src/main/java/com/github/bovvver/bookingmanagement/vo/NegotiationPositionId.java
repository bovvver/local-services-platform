package com.github.bovvver.bookingmanagement.vo;

import java.util.UUID;

public record NegotiationPositionId(UUID value) {
    public NegotiationPositionId {
        if (value == null) {
            throw new IllegalArgumentException("NegotiationPositionId cannot be null");
        }
    }

    public static NegotiationPositionId of(final UUID value) {
        return new NegotiationPositionId(value);
    }

    public static NegotiationPositionId generate() {
        return new NegotiationPositionId(UUID.randomUUID());
    }
}
