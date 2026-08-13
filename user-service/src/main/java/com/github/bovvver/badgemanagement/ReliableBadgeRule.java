package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import com.github.bovvver.vo.BadgeType;
import org.springframework.stereotype.Component;

@Component
class ReliableBadgeRule implements BadgeRule {

    @Override
    public BadgeType getType() {
        return BadgeType.RELIABLE;
    }

    @Override
    public boolean shouldHaveBadge(ReputationUpdated event) {
        int bookingsCount = event.completedBookings() + event.cancelledBookings();

        boolean hasEnoughBookings = bookingsCount >= 20;
        boolean isReliable = event.cancelledBookings() <= bookingsCount * 0.1;

        return hasEnoughBookings && isReliable;
    }
}
