package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class ReputationUpdatedBadgeListener {

    private final BadgeAssignmentService badgeAssignmentService;

    @EventListener
    public void handle(ReputationUpdated event) {
        badgeAssignmentService.addBadges(event);
    }
}
