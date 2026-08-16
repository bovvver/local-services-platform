package com.github.bovvver.reputationmanagement;

import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.ExperienceLevel;
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
class ReputationUserCreatedListenerTest {

    @Mock
    private ReputationReadRepository readRepository;

    @Mock
    private ReputationRepository repository;

    @InjectMocks
    private ReputationUserCreatedListener listener;

    private static final UUID USER_UUID = UUID.randomUUID();

    @Test
    void shouldInitializeReputationWhenReputationDoesNotExist() {
        UserCreated event = new UserCreated(
                UserId.of(USER_UUID),
                new Email("test@example.com"),
                "John",
                "Doe"
        );

        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.empty());

        listener.handle(event);

        verify(readRepository).findByUserId(USER_UUID);
        verify(repository).save(any(Reputation.class));
    }

    @Test
    void shouldNotInitializeReputationWhenReputationAlreadyExists() {
        UserCreated event = new UserCreated(
                UserId.of(USER_UUID),
                new Email("test@example.com"),
                "John",
                "Doe"
        );

        ReputationEntity existingEntity = new ReputationEntity(
                USER_UUID,
                5.0,
                0,
                0,
                0,
                ExperienceLevel.BEGINNER,
                0
        );

        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.of(existingEntity));

        listener.handle(event);

        verify(readRepository).findByUserId(USER_UUID);
        verify(repository, never()).save(any(Reputation.class));
    }
}
