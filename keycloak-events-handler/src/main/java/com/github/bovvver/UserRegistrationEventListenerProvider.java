package com.github.bovvver;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UserRegistrationEventListenerProvider implements EventListenerProvider {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationEventListenerProvider.class);
    private final UserRegistrationService userRegistrationService;

    UserRegistrationEventListenerProvider(final KeycloakSession session) {
        this.userRegistrationService = new UserRegistrationService(session);
    }

    @Override
    public void onEvent(Event event) {
        if (EventType.REGISTER.equals(event.getType())) {
            logger.info("User registration detected: {}", event.getUserId());
            userRegistrationService.handleUserRegistration(event);
        }
    }

    @Override
    public void onEvent(final AdminEvent adminEvent, final boolean b) {
    }

    @Override
    public void close() {
    }
}
