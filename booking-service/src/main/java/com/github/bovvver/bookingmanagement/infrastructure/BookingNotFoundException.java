package com.github.bovvver.bookingmanagement.infrastructure;

import java.util.UUID;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(UUID bookingId) {
        super("Booking not found: %s".formatted(bookingId));
    }
}
