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
                booking.getStatus(),
                booking.getProposedSalary().value(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }

    public static Booking toDomain(BookingEntity bookingEntity) {
        return new Booking(
                new BookingId(bookingEntity.getId()),
                new UserId(bookingEntity.getUserId()),
                new OfferId(bookingEntity.getOfferId()),
                bookingEntity.getStatus(),
                new Salary(bookingEntity.getProposedSalary()),
                bookingEntity.getCreatedAt(),
                bookingEntity.getUpdatedAt()
        );
    }
}
