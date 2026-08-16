package com.github.bovvver.reputationmanagement;

import com.github.bovvver.vo.ExperienceLevel;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.Score;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReputationTest {

    private static final UserId USER_ID = UserId.of(UUID.randomUUID());

    @Test
    void shouldInitializeWithBeginnerLevelAndZeroScore() {
        Reputation reputation = Reputation.initialize(USER_ID);

        assertThat(reputation.getExperienceLevel()).isEqualTo(ExperienceLevel.BEGINNER);
        assertThat(reputation.getExperienceScore().value()).isZero();
    }

    @Test
    void shouldRecalculateExperienceAndLevelCorrectly() {
        // Start with clean state
        Reputation reputation = new Reputation(USER_ID, Rating.of(0.0), 0, 0, 0, ExperienceLevel.BEGINNER, Score.of(0));

        // 1. Beginner: 10 completed, 4.0 rating, 1 canceled
        reputation = new Reputation(USER_ID, Rating.of(4.0), 10, 10, 1, ExperienceLevel.BEGINNER, Score.of(0));
        reputation.incrementCompletedBookings(); // completedBookings becomes 11
        // Score: (11 * 100) * (4.0 / 5.0) - (1 * 200) = 1100 * 0.8 - 200 = 880 - 200 = 680
        assertThat(reputation.getExperienceScore().value()).isEqualTo(680);
        assertThat(reputation.getExperienceLevel()).isEqualTo(ExperienceLevel.BEGINNER);

        // 2. Experienced: 10 completed, 5.0 rating, 0 canceled
        reputation = new Reputation(USER_ID, Rating.of(5.0), 1, 10, 0, ExperienceLevel.BEGINNER, Score.of(0));
        reputation.incrementCompletedBookings(); // completedBookings becomes 11
        // Score: (11 * 100) * (5.0 / 5.0) - 0 = 1100
        assertThat(reputation.getExperienceScore().value()).isEqualTo(1100);
        assertThat(reputation.getExperienceLevel()).isEqualTo(ExperienceLevel.EXPERIENCED);

        // 3. Expert: 60 completed, 4.5 rating, 2 canceled
        reputation = new Reputation(USER_ID, Rating.of(4.5), 2, 60, 2, ExperienceLevel.BEGINNER, Score.of(0));
        reputation.incrementCompletedBookings(); // completedBookings becomes 61
        // Score: (61 * 100) * (4.5 / 5.0) - (2 * 200) = 6100 * 0.9 - 400 = 5490 - 400 = 5090
        assertThat(reputation.getExperienceScore().value()).isEqualTo(5090);
        assertThat(reputation.getExperienceLevel()).isEqualTo(ExperienceLevel.EXPERT);

        // 4. Professional: 160 completed, 4.8 rating, 1 canceled
        reputation = new Reputation(USER_ID, Rating.of(4.8), 5, 160, 1, ExperienceLevel.BEGINNER, Score.of(0));
        reputation.incrementCompletedBookings(); // completedBookings becomes 161
        // Score: (161 * 100) * (4.8 / 5.0) - (1 * 200) = 16100 * 0.96 - 200 = 15456 - 200 = 15256
        assertThat(reputation.getExperienceScore().value()).isEqualTo(15256);
        assertThat(reputation.getExperienceLevel()).isEqualTo(ExperienceLevel.PROFESSIONAL);

        // 5. Non-negative: 1 completed, 5.0 rating, 2 canceled
        reputation = new Reputation(USER_ID, Rating.of(5.0), 1, 1, 2, ExperienceLevel.BEGINNER, Score.of(0));
        reputation.incrementCompletedBookings(); // completedBookings becomes 2
        // Score: (2 * 100) * (5.0 / 5.0) - (2 * 200) = 200 - 400 = -200 -> final score = 0
        assertThat(reputation.getExperienceScore().value()).isZero();
        assertThat(reputation.getExperienceLevel()).isEqualTo(ExperienceLevel.BEGINNER);
    }
}
