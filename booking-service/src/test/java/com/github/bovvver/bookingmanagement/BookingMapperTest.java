package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void shouldMapBookingToEntityCorrectly() {
        Booking booking = new Booking(
                BookingId.of(UUID.randomUUID()),
                UserId.of(UUID.randomUUID()),
                OfferId.of(UUID.randomUUID()),
                NegotiationId.of(UUID.randomUUID()),
                BookingStatus.PENDING,
                Salary.of(50000.0),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        BookingEntity entity = BookingMapper.toEntity(booking);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(booking.getId().value());
        assertThat(entity.getUserId()).isEqualTo(booking.getUserId().value());
        assertThat(entity.getOfferId()).isEqualTo(booking.getOfferId().value());
        assertThat(entity.getNegotiationId()).isEqualTo(booking.getNegotiationId().value());
        assertThat(entity.getStatus()).isEqualTo(booking.getStatus());
        assertThat(entity.getFinalSalary()).isEqualTo(booking.getFinalSalary().value());
        assertThat(entity.getCreatedAt()).isEqualTo(booking.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(booking.getUpdatedAt());
    }

    @Test
    void shouldMapEntityToBookingCorrectly() {
        BookingEntity entity = new BookingEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BookingStatus.ACCEPTED,
                BigDecimal.valueOf(60000),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Booking booking = BookingMapper.toDomain(entity);

        assertThat(booking).isNotNull();
        assertThat(booking.getId().value()).isEqualTo(entity.getId());
        assertThat(booking.getUserId().value()).isEqualTo(entity.getUserId());
        assertThat(booking.getOfferId().value()).isEqualTo(entity.getOfferId());
        assertThat(booking.getNegotiationId().value()).isEqualTo(entity.getNegotiationId());
        assertThat(booking.getStatus()).isEqualTo(entity.getStatus());
        assertThat(booking.getFinalSalary().value()).isEqualTo(entity.getFinalSalary());
        assertThat(booking.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(booking.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
    }

    @Test
    void shouldMapEntityListToBookingListCorrectly() {
        List<BookingEntity> entities = List.of(
                new BookingEntity(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null, BookingStatus.PENDING, BigDecimal.valueOf(50000), LocalDateTime.now(), LocalDateTime.now()),
                new BookingEntity(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BookingStatus.ACCEPTED, BigDecimal.valueOf(60000), LocalDateTime.now(), LocalDateTime.now())
        );

        List<Booking> bookings = BookingMapper.toDomainList(entities);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(entities.size());
        assertThat(bookings.get(0).getId().value()).isEqualTo(entities.get(0).getId());
        assertThat(bookings.get(1).getNegotiationId().value()).isEqualTo(entities.get(1).getNegotiationId());
    }

    @Test
    void shouldMapBookingListToEntityListCorrectly() {
        List<Booking> bookings = List.of(
                new Booking(new BookingId(UUID.randomUUID()), new UserId(UUID.randomUUID()), new OfferId(UUID.randomUUID()), null, BookingStatus.PENDING, Salary.of(50000.0), LocalDateTime.now(), LocalDateTime.now()),
                new Booking(new BookingId(UUID.randomUUID()), new UserId(UUID.randomUUID()), new OfferId(UUID.randomUUID()), new NegotiationId(UUID.randomUUID()), BookingStatus.ACCEPTED, Salary.of(60000.0), LocalDateTime.now(), LocalDateTime.now())
        );

        Iterable<BookingEntity> entities = BookingMapper.toEntityList(bookings);

        assertThat(entities).isNotNull();
        assertThat(((Collection<?>) entities).size()).isEqualTo(bookings.size());
    }
}
