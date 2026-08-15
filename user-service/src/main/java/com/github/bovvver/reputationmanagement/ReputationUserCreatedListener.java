package com.github.bovvver.reputationmanagement;

import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class ReputationUserCreatedListener {

    private final ReputationReadRepository reputationReadRepository;
    private final ReputationRepository reputationRepository;

    @EventListener
    public void handle(UserCreated event) {
        UserId userId = event.userId();

        if (reputationReadRepository.findByUserId(userId.value()).isEmpty()) {
            reputationRepository.save(Reputation.initialize(userId));
            log.debug("Reputation initialised for userId={}", userId);
        }
    }
}
