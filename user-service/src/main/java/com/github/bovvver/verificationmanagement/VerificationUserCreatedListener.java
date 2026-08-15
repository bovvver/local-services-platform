package com.github.bovvver.verificationmanagement;

import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class VerificationUserCreatedListener {

    private final VerificationReadRepository verificationReadRepository;
    private final VerificationRepository verificationRepository;

    @EventListener
    public void handle(UserCreated event) {
        UserId userId = event.userId();

        if (verificationReadRepository.findByUserId(userId.value()).isEmpty()) {
            verificationRepository.save(Verification.initialize(userId));
            log.debug("Verification initialised for userId={}", userId);
        }
    }
}
