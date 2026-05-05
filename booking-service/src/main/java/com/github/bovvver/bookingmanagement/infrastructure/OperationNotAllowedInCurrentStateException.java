package com.github.bovvver.bookingmanagement.infrastructure;

import com.github.bovvver.bookingmanagement.vo.BookingStatus;

public class OperationNotAllowedInCurrentStateException extends RuntimeException {

    public OperationNotAllowedInCurrentStateException(BookingStatus bookingStatus) {
        super("Cannot perform this action on booking with status %s".formatted(bookingStatus));
    }
}
