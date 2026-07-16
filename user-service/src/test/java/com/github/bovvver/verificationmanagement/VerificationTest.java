package com.github.bovvver.verificationmanagement;

import com.github.bovvver.infrastructure.AlreadyVerifiedException;
import com.github.bovvver.infrastructure.EmptyVerificationDataException;
import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VerificationTest {

    private static final UUID USER_UUID = UUID.randomUUID();

    @Test
    void shouldInitializeWithPendingStatus() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));

        assertThat(verification.getUserId().value()).isEqualTo(USER_UUID);
        assertThat(verification.getIdentityStatus()).isEqualTo(VerificationStatus.PENDING);
        assertThat(verification.getVerificationProof()).isNull();
    }

    @Test
    void shouldAddVerificationProofSuccessfully() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));
        VerificationProof proof = new VerificationProof("http://example.com/proof", LocalDateTime.now());

        verification.addVerificationProof(proof);

        assertThat(verification.getVerificationProof()).isEqualTo(proof);
    }

    @Test
    void shouldThrowExceptionWhenAddingNullProof() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));

        assertThrows(EmptyVerificationDataException.class, () -> verification.addVerificationProof(null));
    }

    @Test
    void shouldThrowExceptionWhenAddingProofWithNullUrl() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));
        VerificationProof proof = new VerificationProof(null, LocalDateTime.now());

        assertThrows(EmptyVerificationDataException.class, () -> verification.addVerificationProof(proof));
    }

    @Test
    void shouldThrowExceptionWhenAddingProofWithNullUploadedAt() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));
        VerificationProof proof = new VerificationProof("http://example.com/proof", null);

        assertThrows(EmptyVerificationDataException.class, () -> verification.addVerificationProof(proof));
    }

    @Test
    void shouldThrowExceptionWhenAddingProofToAlreadyVerifiedUser() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));
        verification.addVerificationProof(new VerificationProof("http://example.com/proof", LocalDateTime.now()));
        verification.verify();

        VerificationProof newProof = new VerificationProof("http://example.com/new-proof", LocalDateTime.now());

        assertThrows(AlreadyVerifiedException.class, () -> verification.addVerificationProof(newProof));
    }

    @Test
    void shouldVerifySuccessfullyWhenProofIsPresent() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));
        VerificationProof proof = new VerificationProof("http://example.com/proof", LocalDateTime.now());
        verification.addVerificationProof(proof);

        verification.verify();

        assertThat(verification.getIdentityStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    void shouldThrowExceptionWhenVerifyingWithoutProof() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));

        assertThrows(EmptyVerificationDataException.class, verification::verify);
    }

    @Test
    void shouldThrowExceptionWhenVerifyingAlreadyVerifiedUser() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));
        verification.addVerificationProof(new VerificationProof("http://example.com/proof", LocalDateTime.now()));
        verification.verify();

        assertThrows(AlreadyVerifiedException.class, verification::verify);
    }

    @Test
    void shouldRejectSuccessfully() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));

        verification.reject();

        assertThat(verification.getIdentityStatus()).isEqualTo(VerificationStatus.REJECTED);
    }

    @Test
    void shouldThrowExceptionWhenRejectingAlreadyVerifiedUser() {
        Verification verification = Verification.initialize(UserId.of(USER_UUID));
        verification.addVerificationProof(new VerificationProof("http://example.com/proof", LocalDateTime.now()));
        verification.verify();

        assertThrows(AlreadyVerifiedException.class, verification::reject);
    }
}
