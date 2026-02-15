package com.github.bovvver.offermanagment.events;

import com.github.bovvver.offermanagment.vo.BookingRequestStatus;

import java.util.UUID;

public record BookingAccepted(
        BookingRequestStatus status,
        String message,
        UUID offerId,
        UUID bookingId
) implements IntegrationEvent {

    private static final String EVENT_MESSAGE = "Booking request accepted.";

    public BookingAccepted(UUID offerId, UUID bookingId) {
        this(BookingRequestStatus.ACCEPTED, EVENT_MESSAGE, offerId, bookingId);
    }
}
