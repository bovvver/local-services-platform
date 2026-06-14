package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingEntity toEntity(Booking booking) {

        BookingEntity bookingEntity = new BookingEntity(
                booking.getId().value(),
                booking.getUserId().value(),
                booking.getOfferId().value(),
                null,
                booking.getStatus(),
                booking.getSalary() != null ? booking.getSalary().value() : null,
                booking.getCreatedAt(),
                null,    // managed by JPA auditing, set to null here
                booking.getExpiresAt()
        );

        if (booking.getNegotiation() != null) {
            NegotiationEntity negotiationEntity =
                    NegotiationMapper.toEntity(booking.getNegotiation(), bookingEntity);

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
                entity.getSalary() != null ? new Salary(entity.getSalary()) : null,
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
    }

    public static List<Booking> toDomainList(List<BookingEntity> bookingEntities) {
        return bookingEntities.stream()
                .map(BookingMapper::toDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    static Iterable<BookingEntity> toEntityList(final Iterable<Booking> bookings) {
        return ((Collection<Booking>) bookings).stream()
                .map(BookingMapper::toEntity)
                .toList();
    }
}
