package com.github.bovvver.verificationmanagement;

import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;
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
class VerificationUserCreatedListenerTest {

    @Mock
    private VerificationReadRepository readRepository;

    @Mock
    private VerificationRepository repository;

    @InjectMocks
    private VerificationUserCreatedListener listener;

    private static final UUID USER_UUID = UUID.randomUUID();

    @Test
    void shouldInitializeVerificationWhenVerificationDoesNotExist() {
        UserCreated event = new UserCreated(
                UserId.of(USER_UUID),
                new Email("test@example.com"),
                "John",
                "Doe"
        );

        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.empty());

        listener.handle(event);

        verify(readRepository).findByUserId(USER_UUID);
        verify(repository).save(any(Verification.class));
    }

    @Test
    void shouldNotInitializeVerificationWhenVerificationAlreadyExists() {
        UserCreated event = new UserCreated(
                UserId.of(USER_UUID),
                new Email("test@example.com"),
                "John",
                "Doe"
        );

        VerificationEntity existingEntity = new VerificationEntity(
                USER_UUID,
                VerificationStatus.PENDING
        );

        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.of(existingEntity));

        listener.handle(event);

        verify(readRepository).findByUserId(USER_UUID);
        verify(repository, never()).save(any(Verification.class));
    }
}
