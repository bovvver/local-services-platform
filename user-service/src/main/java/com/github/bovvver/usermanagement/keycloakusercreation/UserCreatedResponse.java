package com.github.bovvver.usermanagement.keycloakusercreation;

public record UserCreatedResponse(
        String userId,
        String email,
        String firstName,
        String lastName
) {
}
