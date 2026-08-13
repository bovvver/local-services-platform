package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
class ReputationUpdatedBadgeListener {

    private final BadgeAssignmentService badgeAssignmentService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReputationUpdated event) {
        badgeAssignmentService.addBadges(event);
    }
}
