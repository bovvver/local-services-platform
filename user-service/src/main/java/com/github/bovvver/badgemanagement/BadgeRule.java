package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import com.github.bovvver.vo.BadgeType;

interface BadgeRule {

    BadgeType getType();

    boolean shouldHaveBadge(ReputationUpdated event);

}
