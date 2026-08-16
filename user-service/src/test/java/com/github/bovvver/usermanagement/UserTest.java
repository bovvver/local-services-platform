package com.github.bovvver.usermanagement;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.infrastructure.AlreadyVerifiedException;
import com.github.bovvver.infrastructure.EmptyVerificationDataException;
import com.github.bovvver.usermanagement.keycloakusercreation.UserCreated;
import com.github.bovvver.usermanagement.verification.VerificationProof;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThat(user.getIdentityStatus()).isEqualTo(VerificationStatus.PENDING);
        assertThat(user.getVerificationProof()).isNull();

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

    @Test
    void shouldAddVerificationProofSuccessfully() {
        User user = User.create(UserId.of(USER_UUID), new Email("john@example.com"), "John", "Doe");
        VerificationProof proof = VerificationProof.of("http://example.com/proof.png");

        user.addVerificationProof(proof);

        assertThat(user.getVerificationProof()).isNotNull();
        assertThat(user.getVerificationProof().url()).isEqualTo("http://example.com/proof.png");
        assertThat(user.getIdentityStatus()).isEqualTo(VerificationStatus.PENDING);
    }

    @Test
    void shouldThrowExceptionWhenAddingEmptyOrNullProof() {
        User user = User.create(UserId.of(USER_UUID), new Email("john@example.com"), "John", "Doe");

        assertThrows(EmptyVerificationDataException.class, () -> user.addVerificationProof(null));
        assertThrows(EmptyVerificationDataException.class, () -> user.addVerificationProof(new VerificationProof(null, java.time.LocalDateTime.now())));
    }

    @Test
    void shouldVerifyUserSuccessfullyWhenProofExists() {
        User user = User.create(UserId.of(USER_UUID), new Email("john@example.com"), "John", "Doe");
        user.addVerificationProof(VerificationProof.of("http://example.com/proof.png"));

        user.verify();

        assertThat(user.getIdentityStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(user.isVerified()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenVerifyingWithoutProof() {
        User user = User.create(UserId.of(USER_UUID), new Email("john@example.com"), "John", "Doe");

        assertThrows(EmptyVerificationDataException.class, user::verify);
    }

    @Test
    void shouldRejectUserSuccessfully() {
        User user = User.create(UserId.of(USER_UUID), new Email("john@example.com"), "John", "Doe");

        user.reject();

        assertThat(user.getIdentityStatus()).isEqualTo(VerificationStatus.REJECTED);
        assertThat(user.isVerified()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenMutatingVerifiedUser() {
        User user = User.create(UserId.of(USER_UUID), new Email("john@example.com"), "John", "Doe");
        user.addVerificationProof(VerificationProof.of("http://example.com/proof.png"));
        user.verify();

        assertThrows(AlreadyVerifiedException.class, () -> user.addVerificationProof(VerificationProof.of("new")));
        assertThrows(AlreadyVerifiedException.class, user::verify);
        assertThrows(AlreadyVerifiedException.class, user::reject);
    }
}
