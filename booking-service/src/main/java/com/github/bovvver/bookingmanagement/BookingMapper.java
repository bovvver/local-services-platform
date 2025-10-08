package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;

public class BookingMapper {

    public static BookingEntity toEntity(Booking booking) {
        return new BookingEntity(
                booking.getId().value(),
                booking.getUserId().value(),
                booking.getOfferId().value(),
                booking.getProposedSalary().value()
        );
    }

    public static Booking toDomain(BookingEntity bookingEntity) {
        return Booking.create(
                BookingId.of(bookingEntity.getId()),
                UserId.of(bookingEntity.getUserId()),
                OfferId.of(bookingEntity.getOfferId()),
                Salary.of(bookingEntity.getProposedSalary())
        );
    }
}
