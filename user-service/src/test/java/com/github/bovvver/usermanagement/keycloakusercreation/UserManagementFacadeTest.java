package com.github.bovvver.usermanagement.keycloakusercreation;

import com.github.bovvver.profilemanagement.ProviderProfile;
import com.github.bovvver.profilemanagement.ProviderProfileRepository;
import com.github.bovvver.reputationmanagement.Reputation;
import com.github.bovvver.reputationmanagement.ReputationRepository;
import com.github.bovvver.usermanagement.User;
import com.github.bovvver.usermanagement.UserRepository;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementFacadeTest {

    private static final String TEST_UUID = "c74819ac-5f74-45e5-9b18-7849d3e0512a";

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProviderProfileRepository providerProfileRepository;

    @Mock
    private ReputationRepository reputationRepository;

    @InjectMocks
    private UserManagementFacade userManagementFacade;

    @Test
    void shouldCreateAndSaveUserSuccessfully() {
        CreateUserCommand command = new CreateUserCommand(TEST_UUID, "test@example.com", "John", "Doe");
        User user = User.create(
                UserId.from(TEST_UUID),
                new Email("test@example.com"),
                "John",
                "Doe"
        );

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(providerProfileRepository.save(any(ProviderProfile.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reputationRepository.save(any(Reputation.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userManagementFacade.createUserFromKeycloak(command);

        assertThat(result).isNotNull();
        assertThat(result.getId().value()).isEqualTo(UUID.fromString(TEST_UUID));
        assertThat(result.getEmail().value()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        verify(userRepository).save(any(User.class));
        verify(providerProfileRepository).save(any(ProviderProfile.class));
        verify(reputationRepository).save(any(Reputation.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        CreateUserCommand command = new CreateUserCommand(
                TEST_UUID,
                "invalid-email",
                "John",
                "Doe"
        );

        assertThrows(IllegalArgumentException.class, () -> userManagementFacade.createUserFromKeycloak(command));
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        CreateUserCommand command = new CreateUserCommand(
                null,
                "test@example.com",
                "John",
                "Doe"
        );

        assertThrows(IllegalArgumentException.class, () -> userManagementFacade.createUserFromKeycloak(command));
        verifyNoInteractions(userRepository);
    }
}
