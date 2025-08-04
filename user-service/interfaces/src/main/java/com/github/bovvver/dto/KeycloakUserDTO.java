package com.github.bovvver.dto;

public record KeycloakUserDTO (
        String keycloakUserId,
        String email,
        String firstName,
        String lastName
) {
}
