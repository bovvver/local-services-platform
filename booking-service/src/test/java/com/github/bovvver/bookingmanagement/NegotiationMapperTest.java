package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.NegotiationId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NegotiationMapperTest {

    @Test
    void shouldMapNegotiationToEntityCorrectly() {
        Negotiation negotiation = new Negotiation(
                NegotiationId.of(UUID.randomUUID()),
                BookingId.of(UUID.randomUUID()),
                List.of()
        );

        NegotiationEntity entity = NegotiationMapper.toEntity(negotiation);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(negotiation.getId().value());
        assertThat(entity.getBookingId()).isEqualTo(negotiation.getBookingId().value());
        assertThat(entity.getStatus()).isEqualTo(negotiation.getStatus());
        assertThat(entity.getStartedAt()).isEqualTo(negotiation.getStartedAt());
        assertThat(entity.getLastUpdatedAt()).isEqualTo(negotiation.getLastUpdatedAt());
    }
}
