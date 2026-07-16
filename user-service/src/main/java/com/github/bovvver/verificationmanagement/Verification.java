package com.github.bovvver.verificationmanagement;

import com.github.bovvver.infrastructure.AlreadyVerifiedException;
import com.github.bovvver.infrastructure.EmptyVerificationDataException;
import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;

/**
 * Verification aggregate — holds the current verification state for a user.
 *
 * <p>Exactly one instance exists per user (userId is the identity).
 * Tracks both identity and professional license statuses independently.
 * Initialised with {@code PENDING} when a {@code UserCreated} event fires.</p>
 */
public class Verification {

    private final UserId userId;
    private VerificationStatus identityStatus;
    private VerificationProof verificationProof;

    Verification(final UserId userId,
                 final VerificationStatus identityStatus,
                 final VerificationProof verificationProof) {
        this.userId = userId;
        this.identityStatus = identityStatus;
        this.verificationProof = verificationProof;
    }

    /**
     * Factory — creates a default verification record for a new user.
     * Identity status start as {@code PENDING} and proof is null.
     *
     * @param userId the owning user's identifier
     * @return a new {@code Verification} with both statuses PENDING
     */
    public static Verification initialize(UserId userId) {
        return new Verification(userId, VerificationStatus.PENDING, null);
    }

    public void addVerificationProof(VerificationProof proof) {
        ensureNotVerified();
        if (proof == null || proof.url() == null || proof.uploadedAt() == null) {
            throw new EmptyVerificationDataException();
        }

        verificationProof = proof;
    }

    public void verify() {
        ensureNotVerified();
        if (verificationProof == null || verificationProof.url() == null || verificationProof.url().isBlank()) {
            throw new EmptyVerificationDataException();
        }
        identityStatus = VerificationStatus.VERIFIED;
    }

    public void reject() {
        ensureNotVerified();
        identityStatus = VerificationStatus.REJECTED;
    }

    private void ensureNotVerified() {
        if (isVerified()) {
            throw new AlreadyVerifiedException();
        }
    }

    private boolean isVerified() {
        return identityStatus == VerificationStatus.VERIFIED;
    }

    public UserId getUserId() {
        return userId;
    }

    public VerificationStatus getIdentityStatus() {
        return identityStatus;
    }

    public VerificationProof getVerificationProof() {
        return verificationProof;
    }
}
