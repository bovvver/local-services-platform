package com.github.bovvver.experiencemanagement;

import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class ExperienceUserCreatedListener {

    private final ExperienceSnapshotReadRepository experienceSnapshotReadRepository;
    private final ExperienceSnapshotRepository experienceSnapshotRepository;

    @EventListener
    public void handle(UserCreated event) {
        UserId userId = event.userId();

        if (experienceSnapshotReadRepository.findByUserId(userId.value()).isEmpty()) {
            experienceSnapshotRepository.save(ExperienceSnapshot.initialize(userId));
            log.debug("ExperienceSnapshot initialised for userId={}", userId);
        }
    }
}
