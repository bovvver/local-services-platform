package com.github.bovvver;

import org.keycloak.events.Event;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

class UserRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);
    private final KeycloakSession session;
    private final HttpClient httpClient;

    public UserRegistrationService(KeycloakSession session) {
        this.session = session;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    void handleUserRegistration(final Event event) {
        CompletableFuture.runAsync(() -> {
            try {
                UserModel user = session.users().getUserById(
                        session.getContext().getRealm(),
                        event.getUserId()
                );

                if (user != null) {
                    sendUserToApplication(user);
                }
            } catch (Exception e) {
                logger.error("Failed to sync user: {}", event.getUserId(), e);
            }
        });
    }

    private void sendUserToApplication(final UserModel user) {
        String applicationUrl = System.getenv("GATEWAY_URL");

        String jsonPayload = String.format(
                "{\"keycloakUserId\":\"%s\",\"email\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\"}",
                user.getId(),
                escapeJson(user.getEmail()),
                escapeJson(user.getFirstName()),
                escapeJson(user.getLastName())
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(applicationUrl + "/user-service/auth/create"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                logger.info("Successfully synced user: {}", user.getId());
            } else {
                logger.error("Failed to sync user {}. Status: {}", user.getId(), response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error syncing user: {}", user.getId(), e);
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"").replace("\\", "\\\\");
    }
}
