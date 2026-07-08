package com.github.bovvver.experiencemanagement;

import com.github.bovvver.vo.ExperienceLevel;
import com.github.bovvver.vo.Score;
import com.github.bovvver.vo.UserId;

/**
 * ExperienceSnapshot aggregate — a calculated projection of a provider's experience level.
 *
 * <p>Derived from completed bookings, reputation, and cancellation rate.
 * <strong>Do NOT calculate manually</strong> — this is updated in response to
 * {@code BookingCompleted} and {@code ReviewAdded} events from booking-service.</p>
 *
 * <p>Initialized with {@code BEGINNER / score=0} when a {@code UserCreated} event fires.</p>
 */
public class ExperienceSnapshot {

    private final UserId userId;
    private ExperienceLevel level;
    private Score score;

    ExperienceSnapshot(final UserId userId,
                       final ExperienceLevel level,
                       final Score score) {
        this.userId = userId;
        this.level = level;
        this.score = score;
    }

    /**
     * Factory — creates a default snapshot for a new user.
     *
     * @param userId the owning user's identifier
     * @return a new snapshot at BEGINNER level with score 0
     */
    public static ExperienceSnapshot initialize(UserId userId) {
        return new ExperienceSnapshot(userId, ExperienceLevel.BEGINNER, Score.of(0));
    }

    public UserId getUserId() {
        return userId;
    }

    public ExperienceLevel getLevel() {
        return level;
    }

    public Score getScore() {
        return score;
    }
}
