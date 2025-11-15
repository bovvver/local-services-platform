package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NegotiationPositionMapperTest {

    @Test
    void shouldMapNegotiationPositionToEntityCorrectly() {
        NegotiationPosition negotiationPosition = NegotiationPosition.create(
                NegotiationPositionId.of(UUID.randomUUID()),
                NegotiationId.of(UUID.randomUUID()),
                Salary.of(1000.0),
                NegotiationParty.AUTHOR
        );

        NegotiationPositionEntity entity = NegotiationPositionMapper.toEntity(negotiationPosition);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(negotiationPosition.getId().value());
        assertThat(entity.getNegotiationId()).isEqualTo(negotiationPosition.getNegotiationId().value());
        assertThat(entity.getProposedSalary()).isEqualTo(negotiationPosition.getProposedSalary().value());
        assertThat(entity.getProposedBy()).isEqualTo(negotiationPosition.getProposedBy());
        assertThat(entity.getProposedAt()).isEqualTo(negotiationPosition.getProposedAt());
    }
}
