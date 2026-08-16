package com.github.bovvver.usermanagement.verification;

import com.github.bovvver.infrastructure.UserNotFoundException;
import com.github.bovvver.shared.CurrentUser;
import com.github.bovvver.usermanagement.User;
import com.github.bovvver.usermanagement.UserRepository;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationProcessingServiceTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VerificationProcessingService service;

    private static final UUID USER_UUID = UUID.randomUUID();

    @Test
    void shouldSendVerificationDataSuccessfully() {
        User user = User.create(UserId.of(USER_UUID), new Email("test@example.com"), "John", "Doe");

        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));
        when(userRepository.findById(UserId.of(USER_UUID))).thenReturn(Optional.of(user));

        VerificationDataRequest request = new VerificationDataRequest(List.of("http://example.com/proof"));
        VerificationDataResponse response = service.sendVerificationData(request);

        assertThat(response.userId()).isEqualTo(USER_UUID);
        assertThat(user.getVerificationProof()).isNotNull();
        assertThat(user.getVerificationProof().url()).isEqualTo("http://example.com/proof");
        verify(userRepository).save(user);
    }

    @Test
    void shouldVerifyUserSuccessfully() {
        User user = User.create(UserId.of(USER_UUID), new Email("test@example.com"), "John", "Doe");
        user.addVerificationProof(VerificationProof.of("http://example.com/proof"));

        when(userRepository.findById(UserId.of(USER_UUID))).thenReturn(Optional.of(user));

        service.verify(USER_UUID);

        assertThat(user.getIdentityStatus()).isEqualTo(VerificationStatus.VERIFIED);
        verify(userRepository).save(user);
    }

    @Test
    void shouldRejectUserSuccessfully() {
        User user = User.create(UserId.of(USER_UUID), new Email("test@example.com"), "John", "Doe");

        when(userRepository.findById(UserId.of(USER_UUID))).thenReturn(Optional.of(user));

        service.reject(USER_UUID);

        assertThat(user.getIdentityStatus()).isEqualTo(VerificationStatus.REJECTED);
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.verify(USER_UUID));
    }
}
