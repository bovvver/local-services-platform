package com.github.bovvver.usermanagement;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private static final UUID USER_UUID = UUID.randomUUID();

    @Test
    void shouldCreateUserSuccessfully() {
        User user = User.create(
                UserId.of(USER_UUID),
                new Email("john@example.com"),
                "John",
                "Doe"
        );

        assertThat(user).isNotNull();
        assertThat(user.getId().value()).isEqualTo(USER_UUID);
        assertThat(user.getEmail().value()).isEqualTo("john@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");

        List<DomainEvent> events = user.pullDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(UserCreated.class);

        UserCreated event = (UserCreated) events.getFirst();
        assertThat(event.userId().value()).isEqualTo(USER_UUID);
        assertThat(event.email().value()).isEqualTo("john@example.com");
        assertThat(event.firstName()).isEqualTo("John");
        assertThat(event.lastName()).isEqualTo("Doe");

        // Subsequent call to pullDomainEvents should be empty
        assertThat(user.pullDomainEvents()).isEmpty();
    }
}
