package com.github.bovvver;

import java.util.UUID;

record BookingId(UUID value) {

    BookingId {
        if (value == null) {
            throw new IllegalArgumentException("BookingId cannot be null");
        }
    }

    static BookingId generate() {
        return new BookingId(UUID.randomUUID());
    }
}