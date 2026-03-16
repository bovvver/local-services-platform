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

        Booking booking = Booking.create(
                UserId.of(UUID.randomUUID()),
                OfferId.of(UUID.randomUUID()),
                Salary.of(50000.0)
        );
        booking.beginNegotiation(Salary.of(55000.0));

        BookingEntity entity = BookingMapper.toEntity(booking);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(booking.getId().value());
        assertThat(entity.getUserId()).isEqualTo(booking.getUserId().value());
        assertThat(entity.getOfferId()).isEqualTo(booking.getOfferId().value());
        assertThat(entity.getNegotiation()).isNotNull();
        assertThat(entity.getNegotiation().getId()).isEqualTo(booking.getNegotiation().getId().value());
        assertThat(entity.getNegotiation().getPositions()).hasSize(1);
        assertThat(entity.getNegotiation().getPositions().getFirst().getProposedSalary()).isEqualTo(BigDecimal.valueOf(55000.0));
        assertThat(entity.getStatus()).isEqualTo(booking.getStatus());
        assertThat(entity.getSalary()).isEqualTo(booking.getSalary().value());
        assertThat(entity.getCreatedAt()).isEqualTo(booking.getCreatedAt());
    }

    @Test
    void shouldMapEntityToBookingCorrectly() {
        UUID bookingId = UUID.randomUUID();
        UUID negotiationId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();

        NegotiationEntity negotiationEntity = new NegotiationEntity();
        negotiationEntity.setId(negotiationId);
        negotiationEntity.setStatus(NegotiationStatus.ACTIVE);
        negotiationEntity.setStartedAt(LocalDateTime.now());
        negotiationEntity.setLastUpdatedAt(LocalDateTime.now());

        NegotiationPositionEntity positionEntity = new NegotiationPositionEntity();
        positionEntity.setId(positionId);
        positionEntity.setNegotiation(negotiationEntity);
        positionEntity.setProposedSalary(BigDecimal.valueOf(55000));
        positionEntity.setProposedBy(NegotiationParty.AUTHOR);
        positionEntity.setProposedAt(LocalDateTime.now());

        negotiationEntity.setPositions(List.of(positionEntity));

        BookingEntity entity = new BookingEntity(
                bookingId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                negotiationEntity,
                BookingStatus.IN_NEGOTIATION,
                BigDecimal.valueOf(60000),
                LocalDateTime.now(),
                null
        );

        negotiationEntity.setBooking(entity);

        Booking booking = BookingMapper.toDomain(entity);

        assertThat(booking).isNotNull();
        assertThat(booking.getId().value()).isEqualTo(entity.getId());
        assertThat(booking.getUserId().value()).isEqualTo(entity.getUserId());
        assertThat(booking.getOfferId().value()).isEqualTo(entity.getOfferId());
        assertThat(booking.getNegotiation()).isNotNull();
        assertThat(booking.getNegotiation().getId().value()).isEqualTo(negotiationId);
        assertThat(booking.getNegotiation().getPositions()).hasSize(1);
        assertThat(booking.getNegotiation().getPositions().getFirst().getProposedSalary().value())
                .isEqualTo(BigDecimal.valueOf(55000));
        assertThat(booking.getStatus()).isEqualTo(entity.getStatus());
        assertThat(booking.getSalary().value()).isEqualTo(entity.getSalary());
        assertThat(booking.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }

    @Test
    void shouldMapEntityListToBookingListCorrectly() {
        BookingEntity entity1 = new BookingEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                BookingStatus.PENDING,
                BigDecimal.valueOf(50000),
                LocalDateTime.now(),
                null
        );

        UUID negotiationId = UUID.randomUUID();
        NegotiationEntity negotiationEntity = new NegotiationEntity();
        negotiationEntity.setId(negotiationId);
        negotiationEntity.setStatus(NegotiationStatus.ACTIVE);
        negotiationEntity.setStartedAt(LocalDateTime.now());
        negotiationEntity.setLastUpdatedAt(LocalDateTime.now());
        negotiationEntity.setPositions(List.of());

        BookingEntity entity2 = new BookingEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                negotiationEntity,
                BookingStatus.IN_NEGOTIATION,
                BigDecimal.valueOf(60000),
                LocalDateTime.now(),
                null
        );

        negotiationEntity.setBooking(entity2);

        List<BookingEntity> entities = List.of(entity1, entity2);

        List<Booking> bookings = BookingMapper.toDomainList(entities);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(entities.size());
        assertThat(bookings.getFirst().getId().value()).isEqualTo(entities.getFirst().getId());
        assertThat(bookings.getFirst().getNegotiation()).isNull();
        assertThat(bookings.get(1).getNegotiation()).isNotNull();
        assertThat(bookings.get(1).getNegotiation().getId().value()).isEqualTo(negotiationId);
    }

    @Test
    void shouldMapBookingListToEntityListCorrectly() {
        Booking booking1 = new Booking(
                new BookingId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                new OfferId(UUID.randomUUID()),
                null,
                BookingStatus.PENDING,
                Salary.of(50000.0),
                LocalDateTime.now()
        );

        BookingId bookingId2 = new BookingId(UUID.randomUUID());
        NegotiationId negotiationId = NegotiationId.generate();
        NegotiationPosition position = NegotiationPosition.create(
                negotiationId,
                Salary.of(55000.0),
                NegotiationParty.AUTHOR
        );
        Negotiation negotiation = new Negotiation(
                negotiationId,
                bookingId2,
                List.of(position)
        );

        Booking booking2 = new Booking(
                bookingId2,
                new UserId(UUID.randomUUID()),
                new OfferId(UUID.randomUUID()),
                negotiation,
                BookingStatus.IN_NEGOTIATION,
                Salary.of(60000.0),
                LocalDateTime.now()
        );

        List<Booking> bookings = List.of(booking1, booking2);

        Iterable<BookingEntity> entities = BookingMapper.toEntityList(bookings);

        assertThat(entities).isNotNull();
        assertThat(((Collection<?>) entities).size()).isEqualTo(bookings.size());

        List<BookingEntity> entityList = (List<BookingEntity>) entities;
        assertThat(entityList.get(0).getNegotiation()).isNull();
        assertThat(entityList.get(1).getNegotiation()).isNotNull();
        assertThat(entityList.get(1).getNegotiation().getPositions()).hasSize(1);
    }
}
