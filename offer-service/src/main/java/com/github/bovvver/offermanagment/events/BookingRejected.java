package com.github.bovvver.offermanagment.events;

import com.github.bovvver.offermanagment.vo.BookingRequestStatus;

import java.util.UUID;

public record BookingRejected(
        BookingRequestStatus status,
        String message,
        UUID offerId,
        UUID bookingId
) implements IntegrationEvent {

    private static final String EVENT_MESSAGE = "Booking request rejected. Offer cannot accept more bookings.";

    public BookingRejected(final UUID offerId, final UUID bookingId) {
        this(BookingRequestStatus.REJECTED, EVENT_MESSAGE, offerId, bookingId);
    }
}
