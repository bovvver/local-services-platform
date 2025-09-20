package com.github.bovvver.offermanagment.vo;

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
}