package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import com.github.bovvver.vo.BadgeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
class BadgeHandlingDomainService {

    private final List<BadgeRule> rules;

    public BadgeEvaluationResult handleReputationBadges(ReputationUpdated event, List<BadgeType> currentBadgeTypes) {
        List<BadgeType> toAward = new ArrayList<>();
        List<BadgeType> toRevoke = new ArrayList<>();

        for (BadgeRule rule : rules) {
            boolean shouldHave = rule.shouldHaveBadge(event);
            boolean hasAlready = currentBadgeTypes.contains(rule.getType());

            if (shouldHave && !hasAlready) {
                toAward.add(rule.getType());
            } else if (!shouldHave && hasAlready) {
                toRevoke.add(rule.getType());
            }
        }

        return new BadgeEvaluationResult(toAward, toRevoke);
    }
}