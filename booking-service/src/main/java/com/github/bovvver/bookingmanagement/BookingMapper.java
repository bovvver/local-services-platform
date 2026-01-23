package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.*;

import java.util.Collection;
import java.util.List;

public class BookingMapper {

    public static BookingEntity toEntity(Booking booking) {

        BookingEntity bookingEntity = new BookingEntity(
                booking.getId().value(),
                booking.getUserId().value(),
                booking.getOfferId().value(),
                null,
                booking.getStatus(),
                booking.getFinalSalary() != null ? booking.getFinalSalary().value() : null,
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );

        if (booking.getNegotiation() != null) {
            NegotiationEntity negotiationEntity =
                    NegotiationMapper.toEntity(booking.getNegotiation());

            negotiationEntity.setBooking(bookingEntity);
            bookingEntity.setNegotiation(negotiationEntity);
        }

        return bookingEntity;
    }

    public static Booking toDomain(BookingEntity entity) {
        Negotiation negotiation = NegotiationMapper.toDomain(
                entity.getNegotiation(),
                entity.getId()
        );

        return new Booking(
                new BookingId(entity.getId()),
                new UserId(entity.getUserId()),
                new OfferId(entity.getOfferId()),
                negotiation,
                entity.getStatus(),
                entity.getFinalSalary() != null ? new Salary(entity.getFinalSalary()) : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
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
