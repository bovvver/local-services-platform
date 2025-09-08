package com.github.bovvver;

import com.github.bovvver.requests.KeycloakUserRequest;
import com.github.bovvver.responses.UserCreatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class KeycloakAuthRESTIT extends BaseIntegrationTest {

    private static final String CREATE_USER_ENDPOINT = "/keycloak/auth/create";
    private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";

    @Test
    void shouldCreateUserFromKeycloak() {
        KeycloakUserRequest request = new KeycloakUserRequest(
                TEST_USER_ID,
                TEST_EMAIL,
                TEST_FIRST_NAME,
                TEST_LAST_NAME
        );

        ResponseEntity<UserCreatedResponse> response = restTemplate.postForEntity(
                CREATE_USER_ENDPOINT,
                request,
                UserCreatedResponse.class
        );
        UserCreatedResponse user = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(user).isNotNull();
        assertThat(user.userId()).isEqualTo(TEST_USER_ID);
        assertThat(user.email()).isEqualTo(TEST_EMAIL);
        assertThat(user.firstName()).isEqualTo(TEST_FIRST_NAME);
        assertThat(user.lastName()).isEqualTo(TEST_LAST_NAME);
    }
}