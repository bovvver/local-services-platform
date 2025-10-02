package com.github.bovvver.offermanagment.vo;

import java.util.UUID;

public record OfferId(UUID value) {

    public OfferId {
        if (value == null) {
            throw new IllegalArgumentException("OfferId cannot be null");
        }
    }

    public static OfferId of(UUID value) {
        return new OfferId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
