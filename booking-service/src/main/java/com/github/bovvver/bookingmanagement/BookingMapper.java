package com.github.bovvver.bookingmanagement;

public class BookingMapper {

    public static BookingEntity toEntity(Booking booking) {
        return new BookingEntity(
                booking.getId().value(),
                booking.getUserId().value(),
                booking.getOfferId().value(),
                booking.getProposedSalary().value()
        );
    }
}
