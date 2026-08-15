package com.github.bovvver.profilemanagement;

import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class ProfileUserCreatedListener {

    private final ProviderProfileReadRepository providerProfileReadRepository;
    private final ProviderProfileRepository providerProfileRepository;

    @EventListener
    public void handle(UserCreated event) {
        UserId userId = event.userId();

        if (providerProfileReadRepository.findByUserId(userId.value()).isEmpty()) {
            providerProfileRepository.save(ProviderProfile.createFor(userId));
            log.debug("ProviderProfile initialised for userId={}", userId);
        }
    }
}
