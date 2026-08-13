package com.github.bovvver.badgemanagement;

import com.github.bovvver.vo.BadgeType;
import java.util.List;

record BadgeEvaluationResult(
    List<BadgeType> badgesToAward,
    List<BadgeType> badgesToRevoke
) {}
