package com.github.bovvver.bookingmanagement.infrastructure;

import java.util.UUID;

/**
 * Thrown when a booking is attempted to be expired before its expiration date.
 */
public class BookingNotExpiredYetException extends RuntimeException {

    public BookingNotExpiredYetException(UUID bookingId) {
        super("Booking with ID %s cannot be expired yet.".formatted(bookingId));
    }
}
