package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class BadgeAssignmentService {

    private final BadgeRepository badgeRepository;
    private final BadgeHandlingDomainService badgeHandlingDomainService;

    @Transactional
    void addBadges(ReputationUpdated event) {
        List<Badge> badges = badgeHandlingDomainService.handleReputationBadges(event);
        badgeRepository.saveAll(badges);
    }
}
