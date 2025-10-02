package com.github.bovvver.bookingmanagement.vo;

import java.util.UUID;

public record BookingId(UUID value) {

    public BookingId {
        if (value == null) {
            throw new IllegalArgumentException("BookingId cannot be null");
        }
    }

    public static BookingId of(UUID value) {
        return new BookingId(value);
    }
}