package com.github.bovvver.bookingmanagement.bookingcreation;

class BookingDraftMapper {

    static BookingDraftEntity toEntity(BookingDraft draft) {
        return new BookingDraftEntity(
                draft.getBookingId().value(),
                draft.getOfferId().value(),
                draft.getUserId().value(),
                draft.getSalary().value()
        );
    }
}
