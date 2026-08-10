package com.github.bovvver.reputationmanagement;

import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReputationMapperTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void shouldMapEntityToDomain() {
        ReputationEntity entity = new ReputationEntity(
                USER_ID,
                4.8,
                0,
                15,
                1
        );

        Reputation reputation = ReputationMapper.toDomain(entity);

        assertThat(reputation.getUserId().value()).isEqualTo(USER_ID);
        assertThat(reputation.getAverageRating().value()).isEqualTo(4.8);
        assertThat(reputation.getCompletedBookings()).isEqualTo(15);
        assertThat(reputation.getCancelledBookings()).isEqualTo(1);
    }

    @Test
    void shouldMapDomainToEntity() {
        Reputation reputation = new Reputation(
                UserId.of(USER_ID),
                Rating.of(4.8),
                0,
                15,
                1
        );

        ReputationEntity entity = ReputationMapper.toEntity(reputation);

        assertThat(entity.getUserId()).isEqualTo(USER_ID);
        assertThat(entity.getAverageRating()).isEqualTo(4.8);
        assertThat(entity.getCompletedBookings()).isEqualTo(15);
        assertThat(entity.getCancelledBookings()).isEqualTo(1);
    }
}
