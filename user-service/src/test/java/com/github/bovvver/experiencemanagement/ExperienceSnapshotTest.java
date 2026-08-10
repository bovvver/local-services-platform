package com.github.bovvver.experiencemanagement;

import com.github.bovvver.vo.ExperienceLevel;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExperienceSnapshotTest {

    private static final UserId USER_ID = UserId.of(UUID.randomUUID());

    @Test
    void shouldRecalculateExperienceAndLevelCorrectly() {
        ExperienceSnapshot snapshot = ExperienceSnapshot.initialize(USER_ID);

        // 1. Beginner: 0 completed, 0 rating, 0 canceled -> score = 0
        snapshot.recalculateExperience(Rating.of(0.0), 0, 0);
        assertThat(snapshot.getScore().value()).isZero();
        assertThat(snapshot.getLevel()).isEqualTo(ExperienceLevel.BEGINNER);

        // 2. Beginner: 10 completed, 4.0 rating, 1 canceled -> score = 10 * 100 * (4/5) - 200 = 800 - 200 = 600
        snapshot.recalculateExperience(Rating.of(4.0), 10, 1);
        assertThat(snapshot.getScore().value()).isEqualTo(600);
        assertThat(snapshot.getLevel()).isEqualTo(ExperienceLevel.BEGINNER);

        // 3. Experienced: 10 completed, 5.0 rating, 0 canceled -> score = 10 * 100 * (5/5) - 0 = 1000
        snapshot.recalculateExperience(Rating.of(5.0), 10, 0);
        assertThat(snapshot.getScore().value()).isEqualTo(1000);
        assertThat(snapshot.getLevel()).isEqualTo(ExperienceLevel.EXPERIENCED);

        // 4. Expert: 60 completed, 4.5 rating, 2 canceled -> score = 60 * 100 * 0.9 - 400 = 5400 - 400 = 5000
        snapshot.recalculateExperience(Rating.of(4.5), 60, 2);
        assertThat(snapshot.getScore().value()).isEqualTo(5000);
        assertThat(snapshot.getLevel()).isEqualTo(ExperienceLevel.EXPERT);

        // 5. Professional: 160 completed, 4.8 rating, 1 canceled -> score = 160 * 100 * 0.96 - 200 = 15360 - 200 = 15160
        snapshot.recalculateExperience(Rating.of(4.8), 160, 1);
        assertThat(snapshot.getScore().value()).isEqualTo(15160);
        assertThat(snapshot.getLevel()).isEqualTo(ExperienceLevel.PROFESSIONAL);

        // 6. Non-negative: 1 completed, 5.0 rating, 2 canceled -> score = 100 - 400 = -300 -> final score = 0
        snapshot.recalculateExperience(Rating.of(5.0), 1, 2);
        assertThat(snapshot.getScore().value()).isZero();
        assertThat(snapshot.getLevel()).isEqualTo(ExperienceLevel.BEGINNER);
    }
}
