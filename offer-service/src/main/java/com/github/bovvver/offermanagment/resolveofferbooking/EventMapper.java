package com.github.bovvver.offermanagment.resolveofferbooking;

import com.github.bovvver.contracts.BookingAcceptedEvent;
import com.github.bovvver.contracts.BookingRejectedEvent;
import com.github.bovvver.offermanagment.vo.BookingAccepted;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class EventMapper {

    static BookingAcceptedEvent toBookingAcceptedEvent(final BookingAccepted event) {
        return new BookingAcceptedEvent(
                event.getStatus().name(),
                event.getMessage(),
                event.getOfferId().value(),
                event.getUserId().value(),
                event.getBookingId().value(),
                event.getTimestamp()
        );
    }

    static BookingRejectedEvent toBookingRejectedEvent(final com.github.bovvver.offermanagment.vo.BookingRejected event) {
        return new BookingRejectedEvent(
                event.getStatus().name(),
                event.getMessage(),
                event.getOfferId().value(),
                event.getUserId().value(),
                event.getBookingId().value(),
                event.getTimestamp()
        );
    }
}
