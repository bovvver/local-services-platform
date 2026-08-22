package com.github.bovvver.usermanagement;

import com.github.bovvver.infrastructure.AlreadyVerifiedException;
import com.github.bovvver.infrastructure.EmptyVerificationDataException;
import com.github.bovvver.usermanagement.verification.VerificationProof;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;

/**
 * User aggregate — responsible for identity, account lifecycle, and verification status.
 */
public class User {

    private final UserId id;
    private final Email email;
    private final String firstName;
    private final String lastName;
    private VerificationStatus identityStatus;
    private VerificationProof verificationProof;

    User(final UserId id,
         final Email email,
         final String firstName,
         final String lastName,
         final VerificationStatus identityStatus,
         final VerificationProof verificationProof) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.identityStatus = identityStatus;
        this.verificationProof = verificationProof;
    }

    /**
     * Factory method — creates a new user with PENDING status
     *
     * @param id        unique identifier (from Keycloak)
     * @param email     validated email address
     * @param firstName first name
     * @param lastName  last name
     * @return newly constructed {@code User}
     */
    public static User create(
            UserId id,
            Email email,
            String firstName,
            String lastName
    ) {
        return new User(id, email, firstName, lastName, VerificationStatus.PENDING, null);
    }

    public void addVerificationProof(VerificationProof proof) {
        ensureNotVerified();
        if (proof == null || proof.url() == null || proof.uploadedAt() == null) {
            throw new EmptyVerificationDataException();
        }
        this.verificationProof = proof;
    }

    public void verify() {
        ensureNotVerified();
        if (verificationProof == null || verificationProof.url() == null || verificationProof.url().isBlank()) {
            throw new EmptyVerificationDataException();
        }
        this.identityStatus = VerificationStatus.VERIFIED;
    }

    public void reject() {
        ensureNotVerified();
        this.identityStatus = VerificationStatus.REJECTED;
    }

    public boolean isVerified() {
        return identityStatus == VerificationStatus.VERIFIED;
    }

    private void ensureNotVerified() {
        if (isVerified()) {
            throw new AlreadyVerifiedException();
        }
    }

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public VerificationStatus getIdentityStatus() {
        return identityStatus;
    }

    public VerificationProof getVerificationProof() {
        return verificationProof;
    }
}