package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import com.github.bovvver.vo.BadgeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class BadgeAssignmentService {

    private final BadgeReadRepository badgeReadRepository;
    private final BadgeRepository badgeRepository;
    private final BadgeHandlingDomainService badgeHandlingDomainService;

    @Transactional
    void addBadges(ReputationUpdated event) {
        List<BadgeEntity> entities = badgeReadRepository.findAllByUserId(event.userId().value());
        List<BadgeType> currentBadgeTypes = entities.stream()
                .map(BadgeEntity::getBadgeType)
                .toList();

        BadgeEvaluationResult result = badgeHandlingDomainService.handleReputationBadges(event, currentBadgeTypes);

        result.badgesToRevoke().forEach(type ->
                badgeRepository.deleteByUserIdAndType(event.userId(), type)
        );

        List<Badge> newBadges = result.badgesToAward().stream()
                .map(type -> Badge.award(event.userId(), type))
                .toList();
        badgeRepository.saveAll(newBadges);
    }
}
