package com.github.bovvver.bookingmanagement.vo;

import java.util.UUID;

public record NegotiationId(UUID value) {
    public NegotiationId {
        if (value == null) {
            throw new IllegalArgumentException("NegotiationId cannot be null");
        }
    }

    public static NegotiationId of(UUID value) {
        return new NegotiationId(value);
    }

    public static NegotiationId generate() {
        return new NegotiationId(UUID.randomUUID());
    }
}
