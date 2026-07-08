package com.github.bovvver.reputationmanagement;

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
class ReputationUserCreatedListener {

    private final ReputationReadRepository reputationReadRepository;
    private final ReputationRepository reputationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserCreated event) {
        UserId userId = event.userId();

        if (reputationReadRepository.findByUserId(userId.value()).isEmpty()) {
            reputationRepository.save(Reputation.initialize(userId));
            log.debug("Reputation initialised for userId={}", userId);
        }
    }
}
