package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.NegotiationId;
import com.github.bovvver.bookingmanagement.vo.NegotiationParty;
import com.github.bovvver.bookingmanagement.vo.Salary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NegotiationPositionMapperTest {

    @Test
    void shouldMapNegotiationPositionToEntityCorrectly() {
        NegotiationPosition negotiationPosition = NegotiationPosition.create(
                NegotiationId.of(UUID.randomUUID()),
                Salary.of(1000.0),
                NegotiationParty.AUTHOR
        );

        NegotiationPositionEntity entity = NegotiationPositionMapper.toEntity(negotiationPosition);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(negotiationPosition.getId().value());
        assertThat(entity.getProposedSalary()).isEqualTo(negotiationPosition.getProposedSalary().value());
        assertThat(entity.getProposedBy()).isEqualTo(negotiationPosition.getProposedBy());
        assertThat(entity.getProposedAt()).isEqualTo(negotiationPosition.getProposedAt());
    }

    @Test
    void shouldMapEntityToNegotiationPositionCorrectly() {
        UUID negotiationId = UUID.randomUUID();
        UUID positionId = UUID.randomUUID();

        NegotiationEntity negotiationEntity = new NegotiationEntity();
        negotiationEntity.setId(negotiationId);

        NegotiationPositionEntity entity = new NegotiationPositionEntity();
        entity.setId(positionId);
        entity.setNegotiation(negotiationEntity);
        entity.setProposedSalary(BigDecimal.valueOf(1000.0));
        entity.setProposedBy(NegotiationParty.AUTHOR);
        entity.setProposedAt(LocalDateTime.now());

        NegotiationPosition position = NegotiationPositionMapper.toDomain(entity);

        assertThat(position).isNotNull();
        assertThat(position.getId().value()).isEqualTo(positionId);
        assertThat(position.getNegotiationId().value()).isEqualTo(negotiationId);
        assertThat(position.getProposedSalary().value())
                .isEqualTo(BigDecimal.valueOf(1000.0));
        assertThat(position.getProposedBy()).isEqualTo(NegotiationParty.AUTHOR);
        assertThat(position.getProposedAt()).isEqualTo(entity.getProposedAt());
    }

    @Test
    void shouldThrowWhenNegotiationIsNullInEntity() {
        NegotiationPositionEntity entity = new NegotiationPositionEntity();
        entity.setId(UUID.randomUUID());
        entity.setNegotiation(null);
        entity.setProposedSalary(BigDecimal.valueOf(1000.0));
        entity.setProposedBy(NegotiationParty.AUTHOR);
        entity.setProposedAt(LocalDateTime.now());

        Assertions.assertThrows(NullPointerException.class,
                () -> NegotiationPositionMapper.toDomain(entity));
    }
}
