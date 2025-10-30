package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.*;

public class BookingMapper {

    public static BookingEntity toEntity(Booking booking) {
        return new BookingEntity(
                booking.getId().value(),
                booking.getUserId().value(),
                booking.getOfferId().value(),
                booking.getNegotiationId() == null ? null : booking.getNegotiationId().value(),
                booking.getStatus(),
                booking.getFinalSalary().value(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }

    public static Booking toDomain(BookingEntity bookingEntity) {
        return new Booking(
                new BookingId(bookingEntity.getId()),
                new UserId(bookingEntity.getUserId()),
                new OfferId(bookingEntity.getOfferId()),
                bookingEntity.getNegotiationId() == null ? null : new NegotiationId(bookingEntity.getNegotiationId()),
                bookingEntity.getStatus(),
                new Salary(bookingEntity.getFinalSalary()),
                bookingEntity.getCreatedAt(),
                bookingEntity.getUpdatedAt()
        );
    }
}
