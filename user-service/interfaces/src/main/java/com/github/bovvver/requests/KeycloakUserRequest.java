package com.github.bovvver.requests;

public record KeycloakUserRequest(
        String keycloakUserId,
        String email,
        String firstName,
        String lastName
) {
}
