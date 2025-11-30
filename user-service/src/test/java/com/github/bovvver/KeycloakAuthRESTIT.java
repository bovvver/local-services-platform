package com.github.bovvver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.usermanagement.keycloakusercreation.KeycloakUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class KeycloakAuthRESTIT extends BaseIntegrationTest {

    private static final String CREATE_USER_ENDPOINT = "/keycloak/auth/create";
    private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUserFromKeycloak() throws Exception {
        KeycloakUserRequest request = new KeycloakUserRequest(
                TEST_USER_ID,
                TEST_EMAIL,
                TEST_FIRST_NAME,
                TEST_LAST_NAME
        );

        mockMvc.perform(post(CREATE_USER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.firstName").value(TEST_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(TEST_LAST_NAME));
    }
}