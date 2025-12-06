package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.*;

import java.util.Collection;
import java.util.List;

public class BookingMapper {

    public static BookingEntity toEntity(Booking booking) {
        return new BookingEntity(
                booking.getId().value(),
                booking.getUserId().value(),
                booking.getOfferId().value(),
                booking.getNegotiationId() == null ? null : booking.getNegotiationId().value(),
                booking.getStatus(),
                booking.getFinalSalary() != null ? booking.getFinalSalary().value() : null,
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
                bookingEntity.getFinalSalary() != null ? new Salary(bookingEntity.getFinalSalary()) : null,
                bookingEntity.getCreatedAt(),
                bookingEntity.getUpdatedAt()
        );
    }

    public static List<Booking> toDomainList(List<BookingEntity> bookingEntities) {
        return bookingEntities.stream()
                .map(BookingMapper::toDomain)
                .toList();
    }

    static Iterable<BookingEntity> toEntityList(final Iterable<Booking> bookings) {
        return ((Collection<Booking>) bookings).stream()
                .map(BookingMapper::toEntity)
                .toList();
    }
}
