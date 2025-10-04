package com.github.bovvver.offermanagment.resolvebookingdraft;

import com.github.bovvver.contracts.BookingDraftAcceptedEvent;
import com.github.bovvver.contracts.BookingDraftRejectedEvent;
import com.github.bovvver.offermanagment.vo.BookingDraftAccepted;
import com.github.bovvver.offermanagment.vo.BookingDraftRejected;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class EventMapper {

    static BookingDraftAcceptedEvent toBookingAcceptedEvent(final BookingDraftAccepted event) {
        return new BookingDraftAcceptedEvent(
                event.getStatus().name(),
                event.getMessage(),
                event.getOfferId().value(),
                event.getUserId().value(),
                event.getBookingId().value(),
                event.getTimestamp()
        );
    }

    static BookingDraftRejectedEvent toBookingRejectedEvent(final BookingDraftRejected event) {
        return new BookingDraftRejectedEvent(
                event.getStatus().name(),
                event.getMessage(),
                event.getOfferId().value(),
                event.getUserId().value(),
                event.getBookingId().value(),
                event.getTimestamp()
        );
    }
}
