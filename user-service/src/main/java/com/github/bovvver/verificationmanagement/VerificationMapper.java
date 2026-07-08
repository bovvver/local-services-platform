package com.github.bovvver.verificationmanagement;

import com.github.bovvver.vo.UserId;

/**
 * Manual mapper between {@link Verification} and {@link VerificationEntity}.
 */
class VerificationMapper {

    static Verification toDomain(VerificationEntity entity) {
        return new Verification(
                UserId.of(entity.getUserId()),
                entity.getIdentityStatus()
        );
    }

    static VerificationEntity toEntity(Verification verification) {
        return new VerificationEntity(
                verification.getUserId().value(),
                verification.getIdentityStatus()
        );
    }
}
