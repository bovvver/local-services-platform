package com.github.bovvver.profilemanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
import com.github.bovvver.TestSecurityConfig;
import com.github.bovvver.usermanagement.User;
import com.github.bovvver.usermanagement.UserRepository;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.ServiceCategory;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileModificationRESTIT extends BaseIntegrationTest {

    private static final String UPDATE_PROFILE_ENDPOINT = "/update/profile";

    private static final UUID TEST_USER_ID = TestSecurityConfig.TEST_USER_ID;

    @Autowired
    private ProviderProfileRepository providerProfileRepository;

    @Autowired
    private ProviderProfileReadRepository providerProfileReadRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM provider_profiles");
        jdbcTemplate.update("DELETE FROM users");
    }

    private void createTestUser(UUID userId) {
        User user = User.create(UserId.of(userId), new Email("test@example.com"), "John", "Doe");
        userRepository.save(user);
        providerProfileRepository.save(ProviderProfile.createFor(UserId.of(userId)));
    }

    @Test
    void shouldUpdateProfileSuccessfully() throws Exception {
        createTestUser(TEST_USER_ID);

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Experienced local plumber.",
                "Warsaw",
                "PL",
                Set.of(ServiceCategory.CLEANING)
        );

        mockMvc.perform(post(UPDATE_PROFILE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.bio").value("Experienced local plumber."))
                .andExpect(jsonPath("$.city").value("Warsaw"))
                .andExpect(jsonPath("$.country").value("PL"))
                .andExpect(jsonPath("$.categories[0]").value("CLEANING"));

        ProviderProfileEntity updatedEntity = providerProfileReadRepository.findByUserId(TEST_USER_ID).orElseThrow();
        assertThat(updatedEntity.getBio()).isEqualTo("Experienced local plumber.");
        assertThat(updatedEntity.getCity()).isEqualTo("Warsaw");
        assertThat(updatedEntity.getCountry()).isEqualTo("PL");
        assertThat(updatedEntity.getCategories()).containsExactly(ServiceCategory.CLEANING);
    }

    @Test
    void shouldFailWhenBioIsBlank() throws Exception {
        createTestUser(TEST_USER_ID);

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "   ", // blank bio
                "Warsaw",
                "PL",
                Set.of(ServiceCategory.CLEANING)
        );

        mockMvc.perform(post(UPDATE_PROFILE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenBioExceedsMaxLength() throws Exception {
        createTestUser(TEST_USER_ID);

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "A".repeat(1001), // exceeds 1000 limit
                "Warsaw",
                "PL",
                Set.of(ServiceCategory.CLEANING)
        );

        mockMvc.perform(post(UPDATE_PROFILE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenProfileDoesNotExist() throws Exception {
        // Do NOT create a user/profile row — CurrentUser will return TEST_USER_ID
        // but there is no matching profile, so the service throws UserNotFoundException.
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Bio",
                "Warsaw",
                "PL",
                Set.of(ServiceCategory.CLEANING)
        );

        mockMvc.perform(post(UPDATE_PROFILE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
