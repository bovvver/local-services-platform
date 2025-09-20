package com.github.bovvver.usermanagement.vo;

import java.util.UUID;

public record OfferId(UUID value) {

    public OfferId {
        if (value == null) {
            throw new IllegalArgumentException("OfferId cannot be null");
        }
    }
}