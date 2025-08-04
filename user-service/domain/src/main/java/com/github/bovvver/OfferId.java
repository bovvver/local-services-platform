package com.github.bovvver;

import java.util.UUID;

record OfferId(UUID value) {

    OfferId {
        if (value == null) {
            throw new IllegalArgumentException("OfferId cannot be null");
        }
    }

    static OfferId generate() {
        return new OfferId(UUID.randomUUID());
    }
}