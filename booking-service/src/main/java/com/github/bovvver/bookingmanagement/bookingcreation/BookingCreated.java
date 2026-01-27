package com.github.bovvver.bookingmanagement.bookingcreation;

import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;

import java.time.LocalDateTime;

public record BookingCreated(
        String message,
        BookingId bookingId,
        UserId userId,
        OfferId offerId,
        Salary salary,
        LocalDateTime timestamp
) implements DomainEvent {

    private static final String EVENT_MESSAGE = "Booking created.";

    public BookingCreated(
            BookingId bookingId,
            UserId userId,
            OfferId offerId,
            Salary salary
    ) {
        this(EVENT_MESSAGE, bookingId, userId, offerId, salary, LocalDateTime.now());
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
