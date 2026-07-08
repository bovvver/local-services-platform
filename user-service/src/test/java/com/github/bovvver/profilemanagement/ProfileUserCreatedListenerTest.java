package com.github.bovvver.profilemanagement;

import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileUserCreatedListenerTest {

    @Mock
    private ProviderProfileReadRepository readRepository;

    @Mock
    private ProviderProfileRepository repository;

    @InjectMocks
    private ProfileUserCreatedListener listener;

    private static final UUID USER_UUID = UUID.randomUUID();

    @Test
    void shouldInitializeProfileWhenProfileDoesNotExist() {
        UserCreated event = new UserCreated(
                UserId.of(USER_UUID),
                new Email("test@example.com"),
                "John",
                "Doe"
        );

        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.empty());

        listener.handle(event);

        verify(readRepository).findByUserId(USER_UUID);
        verify(repository).save(any(ProviderProfile.class));
    }

    @Test
    void shouldNotInitializeProfileWhenProfileAlreadyExists() {
        UserCreated event = new UserCreated(
                UserId.of(USER_UUID),
                new Email("test@example.com"),
                "John",
                "Doe"
        );

        ProviderProfileEntity existingEntity = new ProviderProfileEntity(
                UUID.randomUUID(),
                USER_UUID,
                "bio",
                "city",
                "US",
                java.util.Set.of()
        );

        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.of(existingEntity));

        listener.handle(event);

        verify(readRepository).findByUserId(USER_UUID);
        verify(repository, never()).save(any(ProviderProfile.class));
    }
}
