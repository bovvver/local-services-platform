package com.github.bovvver.profilemanagement;

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
class ProfileUserCreatedListener {

    private final ProviderProfileReadRepository providerProfileReadRepository;
    private final ProviderProfileRepository providerProfileRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserCreated event) {
        UserId userId = event.userId();

        if (providerProfileReadRepository.findByUserId(userId.value()).isEmpty()) {
            providerProfileRepository.save(ProviderProfile.createFor(userId));
            log.debug("ProviderProfile initialised for userId={}", userId);
        }
    }
}
