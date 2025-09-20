package com.github.bovvver.usermanagement.keycloakusercreation;

import com.github.bovvver.usermanagement.User;
import com.github.bovvver.usermanagement.UserRepository;
import com.github.bovvver.usermanagement.vo.Email;
import com.github.bovvver.usermanagement.vo.UserId;
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

        User result = userManagementFacade.createUserFromKeycloak(command);

        assertThat(result).isNotNull();
        assertThat(result.getId().value()).isEqualTo(UUID.fromString(TEST_UUID));
        assertThat(result.getEmail().value()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        verify(userRepository).save(any(User.class));
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
