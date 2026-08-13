package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import com.github.bovvver.vo.BadgeType;
import org.springframework.stereotype.Component;

@Component
class TopRatedBadgeRule implements BadgeRule {

    @Override
    public BadgeType getType() {
        return BadgeType.TOP_RATED;
    }

    @Override
    public boolean shouldHaveBadge(ReputationUpdated event) {
        return event.averageRating().value() >= 4.5 && event.completedBookings() >= 10;
    }
}
