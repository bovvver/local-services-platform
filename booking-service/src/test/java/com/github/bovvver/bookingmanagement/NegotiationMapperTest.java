package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NegotiationMapperTest {

    @Test
    void shouldMapNegotiationToEntityCorrectly() {
        UUID bookingId = UUID.randomUUID();
        UUID offerAuthorId = UUID.randomUUID();
        Negotiation negotiation = Negotiation.create(BookingId.of(bookingId), UserId.of(offerAuthorId));
        negotiation.addPosition(Salary.of(10_000.0), NegotiationParty.AUTHOR);

        BookingEntity bookingEntity = new BookingEntity(
                bookingId, UUID.randomUUID(), UUID.randomUUID(),
                null, BookingStatus.PENDING, null, LocalDateTime.now(), null,
                LocalDateTime.now().plusDays(14)
        );

        NegotiationEntity entity = NegotiationMapper.toEntity(negotiation, bookingEntity);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(negotiation.getId().value());
        assertThat(entity.getBooking().getId()).isEqualTo(negotiation.getBookingId().value());
        assertThat(entity.getOfferAuthorId()).isEqualTo(negotiation.getOfferAuthorId().value());
        assertThat(entity.getStatus()).isEqualTo(negotiation.getStatus());
        assertThat(entity.getStartedAt()).isEqualTo(negotiation.getStartedAt());
        assertThat(entity.getLastUpdatedAt()).isEqualTo(negotiation.getLastUpdatedAt());
        assertThat(entity.getPositions()).hasSize(1);
        assertThat(entity.getPositions().getFirst().getProposedSalary())
                .isEqualTo(negotiation.getPositions().getFirst().getProposedSalary().value());
    }

    @Test
    void shouldMapEntityToNegotiationCorrectly() {
        UUID bookingId = UUID.randomUUID();
        UUID negotiationId = UUID.randomUUID();
        UUID offerAuthorId = UUID.randomUUID();

        NegotiationEntity negotiationEntity = new NegotiationEntity();
        negotiationEntity.setId(negotiationId);
        negotiationEntity.setOfferAuthorId(offerAuthorId);
        negotiationEntity.setStatus(NegotiationStatus.ACTIVE);
        negotiationEntity.setStartedAt(LocalDateTime.now().minusHours(1));
        negotiationEntity.setLastUpdatedAt(LocalDateTime.now());

        NegotiationPositionEntity positionEntity = new NegotiationPositionEntity();
        positionEntity.setId(UUID.randomUUID());
        positionEntity.setNegotiation(negotiationEntity);
        positionEntity.setProposedSalary(java.math.BigDecimal.valueOf(12_000));
        positionEntity.setProposedBy(NegotiationParty.AUTHOR);
        positionEntity.setProposedAt(LocalDateTime.now().minusMinutes(30));

        negotiationEntity.setPositions(List.of(positionEntity));

        Negotiation negotiation = NegotiationMapper.toDomain(negotiationEntity, bookingId);

        assertThat(negotiation).isNotNull();
        assertThat(negotiation.getId().value()).isEqualTo(negotiationId);
        assertThat(negotiation.getBookingId().value()).isEqualTo(bookingId);
        assertThat(negotiation.getOfferAuthorId().value()).isEqualTo(offerAuthorId);
        assertThat(negotiation.getStatus()).isEqualTo(negotiationEntity.getStatus());
        assertThat(negotiation.getStartedAt()).isEqualTo(negotiationEntity.getStartedAt());
        assertThat(negotiation.getLastUpdatedAt()).isEqualTo(negotiationEntity.getLastUpdatedAt());
        assertThat(negotiation.getPositions()).hasSize(1);
        assertThat(negotiation.getPositions().getFirst().getProposedSalary().value())
                .isEqualTo(positionEntity.getProposedSalary());
        assertThat(negotiation.getPositions().getFirst().getProposedBy())
                .isEqualTo(positionEntity.getProposedBy());
    }

    @Test
    void shouldReturnNullWhenNegotiationIsNull() {
        BookingEntity bookingEntity = new BookingEntity(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                null, BookingStatus.PENDING, null, LocalDateTime.now(), null,
                LocalDateTime.now().plusDays(14)
        );

        NegotiationEntity entity = NegotiationMapper.toEntity(null, bookingEntity);
        assertThat(entity).isNull();
    }

    @Test
    void shouldReturnNullWhenNegotiationEntityIsNull() {
        Negotiation negotiation = NegotiationMapper.toDomain(null, UUID.randomUUID());
        assertThat(negotiation).isNull();
    }
}
