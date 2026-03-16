package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NegotiationTest {

    @Test
    void shouldCreateNegotiationWithDefaultValues() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());

        Negotiation negotiation = Negotiation.create(bookingId);

        assertThat(negotiation).isNotNull();
        assertThat(negotiation.getId()).isNotNull();
        assertThat(negotiation.getBookingId()).isEqualTo(bookingId);
        assertThat(negotiation.getPositions()).isEmpty();
        assertThat(negotiation.getStatus()).isEqualTo(NegotiationStatus.ACTIVE);
        assertThat(negotiation.getStartedAt()).isNotNull();
        assertThat(negotiation.getLastUpdatedAt()).isNotNull();
    }

    @Test
    void shouldAddPositionWhenNegotiationIsActive() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        Negotiation negotiation = Negotiation.create(bookingId);

        Salary proposedSalary = Salary.of(50000.0);
        negotiation.addPosition(proposedSalary, NegotiationParty.AUTHOR);

        assertThat(negotiation.getPositions()).hasSize(1);
        NegotiationPosition position = negotiation.getPositions().getFirst();
        assertThat(position.getNegotiationId()).isEqualTo(negotiation.getId());
        assertThat(position.getProposedSalary()).isEqualTo(proposedSalary);
        assertThat(position.getProposedBy()).isEqualTo(NegotiationParty.AUTHOR);
        assertThat(position.getProposedAt()).isNotNull();
    }

    @Test
    void shouldReturnUnmodifiablePositionsList() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        Negotiation negotiation = Negotiation.create(bookingId);

        negotiation.addPosition(Salary.of(50000.0), NegotiationParty.AUTHOR);
        List<NegotiationPosition> positions = negotiation.getPositions();

        assertThrows(UnsupportedOperationException.class, () -> positions.add(
                NegotiationPosition.create(negotiation.getId(), Salary.of(60000.0), NegotiationParty.AUTHOR))
        );
    }

    @Test
    void shouldThrowExceptionWhenAddingPositionToNonActiveNegotiation() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        Negotiation negotiation = Negotiation.create(bookingId);

        Negotiation nonActiveNegotiation = new Negotiation(
                negotiation.getId(),
                negotiation.getBookingId(),
                negotiation.getPositions(),
                NegotiationStatus.REJECTED,
                negotiation.getStartedAt(),
                LocalDateTime.now()
        );

        assertThrows(IllegalStateException.class,
                () -> nonActiveNegotiation.addPosition(Salary.of(50000.0), NegotiationParty.AUTHOR));
    }
}
