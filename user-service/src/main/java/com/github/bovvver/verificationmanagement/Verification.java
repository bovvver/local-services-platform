package com.github.bovvver.verificationmanagement;

import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;

/**
 * Verification aggregate — holds the current verification state for a user.
 *
 * <p>Exactly one instance exists per user (userId is the identity).
 * Tracks both identity and professional license statuses independently.
 * Initialised with {@code PENDING} for both types when a {@code UserCreated} event fires.</p>
 */
public class Verification {

    private final UserId userId;
    private VerificationStatus identityStatus;

    Verification(final UserId userId,
                 final VerificationStatus identityStatus) {
        this.userId = userId;
        this.identityStatus = identityStatus;
    }

    /**
     * Factory — creates a default verification record for a new user.
     * Both identity and license statuses start as {@code PENDING}.
     *
     * @param userId the owning user's identifier
     * @return a new {@code Verification} with both statuses PENDING
     */
    public static Verification initialize(UserId userId) {
        return new Verification(userId, VerificationStatus.PENDING);
    }

    public UserId getUserId() {
        return userId;
    }

    public VerificationStatus getIdentityStatus() {
        return identityStatus;
    }
}
