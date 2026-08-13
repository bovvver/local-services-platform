package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
class BadgeHandlingDomainService {

    private final BadgeReadRepository badgeReadRepository;
    private final BadgeRepository badgeWriteRepository;
    private final List<BadgeRule> rules;

    List<Badge> handleReputationBadges(ReputationUpdated event) {
        List<Badge> badges = new ArrayList<>();

        for (BadgeRule rule : rules) {
            badges.addAll(syncBadge(event, rule));
        }
        return badges;
    }

    private List<Badge> syncBadge(final ReputationUpdated event, final BadgeRule rule) {
        boolean shouldHaveBadge = rule.shouldHaveBadge(event);
        boolean hasBadge = badgeReadRepository.existsByUserIdAndBadgeType(event.userId().value(), rule.getType());

        if (shouldHaveBadge && !hasBadge) {
            return List.of(Badge.award(event.userId(), rule.getType()));
        }
        if (!shouldHaveBadge && hasBadge) {
            badgeWriteRepository.deleteByUserIdAndType(event.userId(), rule.getType());
        }
        return Collections.emptyList();
    }
}