package com.github.bovvver;

import com.github.bovvver.requests.KeycloakUserRequest;
import com.github.bovvver.responses.UserCreatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling Keycloak authentication-related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/keycloak/auth")
class KeycloakAuthREST {

    private static final String CREATE_USER_ENDPOINT = "/create";
    private final UserManagementFacade userManagementFacade;

    /**
     * Creates a new user based on the data provided by Keycloak SPI.
     *
     * @param keycloakUserRequest the request body containing user details from Keycloak
     * @return a ResponseEntity containing the created user details and HTTP status 201 (Created)
     */
    @PostMapping(path = CREATE_USER_ENDPOINT)
    ResponseEntity<UserCreatedResponse> createUserFromKeycloakSPI(@RequestBody KeycloakUserRequest keycloakUserRequest) {
        User savedUser = userManagementFacade.createUserFromKeycloak(UserTransportationMapper.toCreateUserCommand(keycloakUserRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserTransportationMapper.toUserCreatedResponse(savedUser));
    }
}
