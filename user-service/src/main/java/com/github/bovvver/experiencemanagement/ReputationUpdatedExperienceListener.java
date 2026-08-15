package com.github.bovvver.experiencemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class ReputationUpdatedExperienceListener {

    private final ExperienceModificationService experienceModificationService;

    @EventListener
    public void handle(ReputationUpdated event) {
        experienceModificationService.recalculateExperience(
                event.userId(),
                event.averageRating(),
                event.completedBookings(),
                event.cancelledBookings()
        );
    }
}
