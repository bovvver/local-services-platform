package com.github.bovvver.profilemanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
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

    @Test
    void shouldUpdateProfileSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();
        // Create user first to satisfy foreign key constraint
        User user = User.create(UserId.of(userId), new Email("test@example.com"), "John", "Doe");
        userRepository.save(user);

        // Pre-create the provider profile
        providerProfileRepository.save(ProviderProfile.createFor(UserId.of(userId)));

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                userId,
                "Experienced local plumber.",
                "Warsaw",
                "PL",
                Set.of(ServiceCategory.CLEANING)
        );

        mockMvc.perform(post(UPDATE_PROFILE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.bio").value("Experienced local plumber."))
                .andExpect(jsonPath("$.city").value("Warsaw"))
                .andExpect(jsonPath("$.country").value("PL"))
                .andExpect(jsonPath("$.categories[0]").value("CLEANING"));

        // Verify that database state was updated
        ProviderProfileEntity updatedEntity = providerProfileReadRepository.findByUserId(userId).orElseThrow();
        assertThat(updatedEntity.getBio()).isEqualTo("Experienced local plumber.");
        assertThat(updatedEntity.getCity()).isEqualTo("Warsaw");
        assertThat(updatedEntity.getCountry()).isEqualTo("PL");
        assertThat(updatedEntity.getCategories()).containsExactly(ServiceCategory.CLEANING);
    }

    @Test
    void shouldFailWhenBioIsBlank() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.create(UserId.of(userId), new Email("test@example.com"), "John", "Doe");
        userRepository.save(user);

        providerProfileRepository.save(ProviderProfile.createFor(UserId.of(userId)));

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                userId,
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
        UUID userId = UUID.randomUUID();
        User user = User.create(UserId.of(userId), new Email("test@example.com"), "John", "Doe");
        userRepository.save(user);

        providerProfileRepository.save(ProviderProfile.createFor(UserId.of(userId)));

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                userId,
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
    void shouldFailWhenUserIdIsNull() throws Exception {
        ProfileUpdateRequest request = new ProfileUpdateRequest(
                null,
                "Bio",
                "Warsaw",
                "PL",
                Set.of(ServiceCategory.CLEANING)
        );

        mockMvc.perform(post(UPDATE_PROFILE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
