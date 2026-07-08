package com.github.bovvver.vo;

import java.util.UUID;

public record BadgeId(UUID value) {

    public BadgeId {
        if (value == null) {
            throw new IllegalArgumentException("BadgeId cannot be null");
        }
    }

    public static BadgeId of(UUID value) {
        return new BadgeId(value);
    }

    public static BadgeId generate() {
        return new BadgeId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
