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

/**
 * Service responsible for handling user registration events from Keycloak
 * and synchronizing new users with the application via HTTP requests.
 * <p>
 * Workflow:
 * <ol>
 *     <li>Keycloak emits a {@link org.keycloak.events.Event} when a user registers.</li>
 *     <li>{@link #handleUserRegistration(Event)} asynchronously retrieves the user model.</li>
 *     <li>User data is transformed into JSON and sent to the application gateway.</li>
 * </ol>
 * </p>
 *
 * <p>Environment variables used:</p>
 * <ul>
 *     <li>{@code KEYCLOAK_API_KEY} – API key required for authenticating requests to the gateway</li>
 *     <li>{@code GATEWAY_URL} – base URL of the application gateway</li>
 * </ul>
 */
class UserRegistrationService {

    private static final String API_KEY = System.getenv("KEYCLOAK_API_KEY");
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);
    private final KeycloakSession session;
    private final HttpClient httpClient;

    public UserRegistrationService(KeycloakSession session) {
        this.session = session;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Handles a user registration event by asynchronously retrieving user data
     * and sending it to the application gateway.
     *
     * @param event Keycloak event containing user registration details
     */
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

    /**
     * Sends user details from Keycloak to the application gateway.
     * <p>
     * Request details:
     * <ul>
     *     <li>Method: {@code POST}</li>
     *     <li>URL: {@code {GATEWAY_URL}/user-service/keycloak/auth/create}</li>
     *     <li>Headers:
     *         <ul>
     *             <li>{@code X-Keycloak-API-Key} – API key</li>
     *             <li>{@code Content-Type} – application/json</li>
     *         </ul>
     *     </li>
     *     <li>Body: JSON with {@code keycloakUserId}, {@code email}, {@code firstName}, {@code lastName}</li>
     * </ul>
     * </p>
     *
     * @param user the {@link UserModel} from Keycloak to be synchronized
     */
    private void sendUserToApplication(final UserModel user) {
        String applicationUrl = System.getenv("GATEWAY_URL");

        String jsonPayload = String.format(
                "{\"keycloakUserId\":\"%s\",\"email\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\"}",
                user.getId(),
                escapeJson(user.getEmail()),
                escapeJson(user.getFirstName()),
                escapeJson(user.getLastName())
        );

        logger.info("Preparing to send request to: {}", applicationUrl);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(applicationUrl + "/user-service/keycloak/auth/create"))
                    .header("X-Keycloak-API-Key", API_KEY)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
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

    /**
     * Escapes special characters in a string to make it JSON-safe.
     *
     * @param value original string
     * @return escaped string or empty string if input was {@code null}
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"").replace("\\", "\\\\");
    }
}
