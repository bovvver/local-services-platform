package com.github.bovvver.experiencemanagement;

import com.github.bovvver.vo.ExperienceLevel;
import com.github.bovvver.vo.Score;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExperienceSnapshotMapperTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void shouldMapEntityToDomain() {
        ExperienceSnapshotEntity entity = new ExperienceSnapshotEntity(
                USER_ID,
                ExperienceLevel.EXPERIENCED,
                150
        );

        ExperienceSnapshot snapshot = ExperienceSnapshotMapper.toDomain(entity);

        assertThat(snapshot.getUserId().value()).isEqualTo(USER_ID);
        assertThat(snapshot.getLevel()).isEqualTo(ExperienceLevel.EXPERIENCED);
        assertThat(snapshot.getScore().value()).isEqualTo(150);
    }

    @Test
    void shouldMapDomainToEntity() {
        ExperienceSnapshot snapshot = new ExperienceSnapshot(
                UserId.of(USER_ID),
                ExperienceLevel.EXPERIENCED,
                Score.of(150)
        );

        ExperienceSnapshotEntity entity = ExperienceSnapshotMapper.toEntity(snapshot);

        assertThat(entity.getUserId()).isEqualTo(USER_ID);
        assertThat(entity.getLevel()).isEqualTo(ExperienceLevel.EXPERIENCED);
        assertThat(entity.getScore()).isEqualTo(150);
    }
}
