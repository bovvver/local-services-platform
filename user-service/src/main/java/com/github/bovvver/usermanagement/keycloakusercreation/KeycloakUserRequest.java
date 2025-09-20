package com.github.bovvver.usermanagement.keycloakusercreation;

public record KeycloakUserRequest(
        String keycloakUserId,
        String email,
        String firstName,
        String lastName
) {
}
