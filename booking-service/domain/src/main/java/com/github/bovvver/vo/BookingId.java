package com.github.bovvver.vo;

import java.util.UUID;

public record BookingId(UUID value) {

    public BookingId {
        if (value == null) {
            throw new IllegalArgumentException("BookingId cannot be null");
        }
    }
}