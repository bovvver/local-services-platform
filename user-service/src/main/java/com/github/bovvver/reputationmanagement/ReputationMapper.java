package com.github.bovvver.reputationmanagement;

import com.github.bovvver.vo.Rating;
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
                entity.getCancelledBookings()
        );
    }

    static ReputationEntity toEntity(Reputation reputation) {
        return new ReputationEntity(
                reputation.getUserId().value(),
                reputation.getAverageRating().value(),
                reputation.getTotalRatings(),
                reputation.getCompletedBookings(),
                reputation.getCancelledBookings()
        );
    }
}
