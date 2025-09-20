package com.github.bovvver.usermanagement.keycloakusercreation;

public record CreateUserCommand(String userId,
                         String email,
                         String firstName,
                         String lastName) {
}
