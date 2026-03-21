package com.github.bovvver.bookingmanagement.infrastructure;

/**
 * Thrown when a booking decision request violates business validation rules
 * (e.g. salary missing for NEGOTIATE or provided for non-NEGOTIATE).
 */
public class BookingDecisionValidationException extends RuntimeException {

    public BookingDecisionValidationException(String message) {
        super(message);
    }
}
