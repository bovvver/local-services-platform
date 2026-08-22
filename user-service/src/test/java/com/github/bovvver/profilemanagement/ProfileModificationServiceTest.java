package com.github.bovvver.profilemanagement;

import com.github.bovvver.infrastructure.UserNotFoundException;
import com.github.bovvver.shared.CurrentUser;
import com.github.bovvver.vo.ServiceCategory;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileModificationServiceTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private ProviderProfileReadRepository providerProfileReadRepository;

    @Mock
    private ProviderProfileRepository providerProfileRepository;

    @InjectMocks
    private ProfileModificationService service;

    @Test
    void shouldUpdateProfileSuccessfully() {
        UUID userId = UUID.randomUUID();
        when(currentUser.getId()).thenReturn(UserId.of(userId));

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Experienced local plumber.",
                "Warsaw",
                "PL",
                Set.of(ServiceCategory.CLEANING)
        );

        ProviderProfileEntity profileEntity = new ProviderProfileEntity(
                UUID.randomUUID(),
                userId,
                null,
                null,
                null,
                Set.of()
        );

        when(providerProfileReadRepository.findByUserId(userId)).thenReturn(Optional.of(profileEntity));
        when(providerProfileRepository.save(any(ProviderProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileUpdateResponse response = service.updateProfile(request);

        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.bio()).isEqualTo("Experienced local plumber.");
        assertThat(response.city()).isEqualTo("Warsaw");
        assertThat(response.country()).isEqualTo("PL");
        assertThat(response.categories()).containsExactly(ServiceCategory.CLEANING);

        verify(providerProfileRepository).save(any(ProviderProfile.class));
    }

    @Test
    void shouldThrowExceptionWhenProfileNotFound() {
        UUID userId = UUID.randomUUID();
        when(currentUser.getId()).thenReturn(UserId.of(userId));

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "Bio",
                "Warsaw",
                "PL",
                Set.of()
        );

        when(providerProfileReadRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProfile(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Profile not found for userId=" + userId);

        verify(providerProfileRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenBioIsBlank() {
        UUID userId = UUID.randomUUID();
        when(currentUser.getId()).thenReturn(UserId.of(userId));

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "   ",
                "Warsaw",
                "PL",
                Set.of()
        );

        ProviderProfileEntity profileEntity = new ProviderProfileEntity(
                UUID.randomUUID(),
                userId,
                null,
                null,
                null,
                Set.of()
        );

        when(providerProfileReadRepository.findByUserId(userId)).thenReturn(Optional.of(profileEntity));

        assertThatThrownBy(() -> service.updateProfile(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bio cannot be blank");

        verify(providerProfileRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenBioExceedsMaxLength() {
        UUID userId = UUID.randomUUID();
        when(currentUser.getId()).thenReturn(UserId.of(userId));

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "A".repeat(1001),
                "Warsaw",
                "PL",
                Set.of()
        );

        ProviderProfileEntity profileEntity = new ProviderProfileEntity(
                UUID.randomUUID(),
                userId,
                null,
                null,
                null,
                Set.of()
        );

        when(providerProfileReadRepository.findByUserId(userId)).thenReturn(Optional.of(profileEntity));

        assertThatThrownBy(() -> service.updateProfile(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bio cannot exceed 1000 characters");

        verify(providerProfileRepository, never()).save(any());
    }
}
