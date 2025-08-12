package com.github.bovvver.responses;

public record UserCreatedResponse(
        String userId,
        String email,
        String firstName,
        String lastName
) {
}
