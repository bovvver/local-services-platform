package com.github.bovvver.usermanagement.keycloakusercreation;

import com.github.bovvver.usermanagement.User;
import com.github.bovvver.usermanagement.vo.Email;
import com.github.bovvver.usermanagement.vo.UserId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTransportationMapperTest {

    private static final String TEST_UUID = "123e4567-e89b-12d3-a456-426614174000";

    @Test
    void shouldMapKeycloakUserRequestToCreateUserCommand() {
        KeycloakUserRequest request = new KeycloakUserRequest(
                TEST_UUID,
                "test@example.com",
                "John",
                "Doe"
        );

        CreateUserCommand command = UserTransportationMapper.toCreateUserCommand(request);

        assertThat(command.userId()).isEqualTo(TEST_UUID);
        assertThat(command.email()).isEqualTo("test@example.com");
        assertThat(command.firstName()).isEqualTo("John");
        assertThat(command.lastName()).isEqualTo("Doe");
    }

    @Test
    void shouldMapUserToUserCreatedResponse() {
        User user = User.create(
                UserId.from(TEST_UUID),
                new Email("test@example.com"),
                "John",
                "Doe"
        );

        UserCreatedResponse response = UserTransportationMapper.toUserCreatedResponse(user);

        assertThat(response.userId()).isEqualTo(TEST_UUID);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void shouldThrowExceptionWhenKeycloakUserRequestIsNull() {
        assertThrows(NullPointerException.class, () -> UserTransportationMapper.toCreateUserCommand(null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void shouldThrowExceptionWhenUserIsNull() {
        assertThrows(NullPointerException.class, () -> UserTransportationMapper.toUserCreatedResponse(null));
    }
}