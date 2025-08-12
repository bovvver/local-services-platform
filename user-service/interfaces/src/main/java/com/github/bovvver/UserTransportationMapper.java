package com.github.bovvver;

import com.github.bovvver.requests.KeycloakUserRequest;
import com.github.bovvver.public_commands.CreateUserCommand;
import com.github.bovvver.responses.UserCreatedResponse;

class UserTransportationMapper {

    static CreateUserCommand toCreateUserCommand(KeycloakUserRequest keycloakUserRequest) {
        return new CreateUserCommand(
                keycloakUserRequest.keycloakUserId(),
                keycloakUserRequest.email(),
                keycloakUserRequest.firstName(),
                keycloakUserRequest.lastName()
        );
    }

    static UserCreatedResponse toUserCreatedResponse(User user) {
        return new UserCreatedResponse(
                user.getId().toString(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}
