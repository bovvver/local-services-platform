package com.github.bovvver.experiencemanagement;

import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.vo.UserId;
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
class ExperienceUserCreatedListener {

    private final ExperienceSnapshotReadRepository experienceSnapshotReadRepository;
    private final ExperienceSnapshotRepository experienceSnapshotRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserCreated event) {
        UserId userId = event.userId();

        if (experienceSnapshotReadRepository.findByUserId(userId.value()).isEmpty()) {
            experienceSnapshotRepository.save(ExperienceSnapshot.initialize(userId));
            log.debug("ExperienceSnapshot initialised for userId={}", userId);
        }
    }
}
