package com.github.bovvver.bookingmanagement.infrastructure;

public class InvalidBookingStatusException extends RuntimeException {

    public InvalidBookingStatusException(String message) {
        super(message);
    }
}
