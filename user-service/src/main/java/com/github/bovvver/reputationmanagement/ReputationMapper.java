package com.github.bovvver.reputationmanagement;

import com.github.bovvver.vo.ExperienceLevel;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.Score;
import com.github.bovvver.vo.UserId;

/**
 * Manual mapper between {@link Reputation} and {@link ReputationEntity}.
 */
class ReputationMapper {

    static Reputation toDomain(ReputationEntity entity) {
        return new Reputation(
                UserId.of(entity.getUserId()),
                Rating.of(entity.getAverageRating()),
                entity.getTotalRatings(),
                entity.getCompletedBookings(),
                entity.getCancelledBookings(),
                entity.getExperienceLevel(),
                Score.of(entity.getExperienceScore())
        );
    }

    static ReputationEntity toEntity(Reputation reputation) {
        return new ReputationEntity(
                reputation.getUserId().value(),
                reputation.getAverageRating().value(),
                reputation.getTotalRatings(),
                reputation.getCompletedBookings(),
                reputation.getCancelledBookings(),
                reputation.getExperienceLevel(),
                reputation.getExperienceScore().value()
        );
    }
}
