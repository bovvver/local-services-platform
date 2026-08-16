package com.github.bovvver.usermanagement;

import com.github.bovvver.usermanagement.verification.VerificationProof;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;

import java.time.LocalDateTime;

/**
 * Manual mapper between the {@link User} domain aggregate and the {@link UserEntity} JPA entity.
 */
class UserMapper {

    /**
     * Maps a {@link UserEntity} to the {@link User} domain aggregate.
     *
     * @param entity the JPA entity to map
     * @return the domain aggregate
     */
    static User toDomain(UserEntity entity) {
        VerificationProof proof = null;
        if (entity.getProofUrl() != null) {
            proof = new VerificationProof(entity.getProofUrl(), entity.getProofUploadedAt());
        }
        return new User(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getIdentityStatus(),
                proof
        );
    }

    /**
     * Maps a {@link User} domain aggregate to a {@link UserEntity} for persistence.
     *
     * @param user the domain aggregate to map
     * @return the JPA entity
     */
    static UserEntity toEntity(User user) {
        String proofUrl = user.getVerificationProof() != null ? user.getVerificationProof().url() : null;
        LocalDateTime proofUploadedAt = user.getVerificationProof() != null ? user.getVerificationProof().uploadedAt() : null;
        return new UserEntity(
                user.getId().value(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName(),
                user.getIdentityStatus(),
                proofUrl,
                proofUploadedAt
        );
    }
}
