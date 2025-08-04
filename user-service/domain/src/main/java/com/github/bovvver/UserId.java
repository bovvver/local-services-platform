package com.github.bovvver;

import java.util.UUID;

record UserId(UUID value) {

    UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    static UserId of(UUID value) {
        return new UserId(value);
    }
}