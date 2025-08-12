package com.github.bovvver.vo;

import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId string cannot be null or blank");
        }
        return new UserId(UUID.fromString(value));
    }
}