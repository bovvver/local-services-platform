package com.github.bovvver.usermanagement.keycloakusercreation;

import com.github.bovvver.usermanagement.User;

class UserTransportationMapper {

    /**
     * Maps a KeycloakUserRequest object to a CreateUserCommand object.
     *
     * @param keycloakUserRequest the request object containing user details from Keycloak
     * @return a CreateUserCommand object containing the mapped user details
     */
    static CreateUserCommand toCreateUserCommand(KeycloakUserRequest keycloakUserRequest) {
        return new CreateUserCommand(
                keycloakUserRequest.keycloakUserId(),
                keycloakUserRequest.email(),
                keycloakUserRequest.firstName(),
                keycloakUserRequest.lastName()
        );
    }

    /**
     * Maps a User object to a UserCreatedResponse object.
     *
     * @param user the User object containing the user details
     * @return a UserCreatedResponse object containing the mapped user details
     */
    static UserCreatedResponse toUserCreatedResponse(User user) {
        return new UserCreatedResponse(
                user.getId().toString(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}
