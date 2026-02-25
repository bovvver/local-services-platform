package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingAcceptedFailureIntegrationEvent;

public class BookingAcceptedEventMapper {

    public static BookingAcceptedFailureIntegrationEvent toIntegrationEvent(BookingAcceptedFailure event) {
        return new BookingAcceptedFailureIntegrationEvent(
                event.bookingId()
        );
    }
}
