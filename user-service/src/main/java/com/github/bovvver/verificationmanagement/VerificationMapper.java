package com.github.bovvver.verificationmanagement;

import com.github.bovvver.vo.UserId;

import java.time.LocalDateTime;

/**
 * Manual mapper between {@link Verification} and {@link VerificationEntity}.
 */
public class VerificationMapper {

    public static Verification toDomain(VerificationEntity entity) {
        VerificationProof proof = entity.getProofUrl() == null ? null :
                new VerificationProof(entity.getProofUrl(), entity.getProofUploadedAt());
        return new Verification(
                UserId.of(entity.getUserId()),
                entity.getIdentityStatus(),
                proof
        );
    }

    public static VerificationEntity toEntity(Verification verification) {
        String proofUrl = verification.getVerificationProof() == null ? null : verification.getVerificationProof().url();
        LocalDateTime proofUploadedAt = verification.getVerificationProof() == null ? null : verification.getVerificationProof().uploadedAt();
        return new VerificationEntity(
                verification.getUserId().value(),
                verification.getIdentityStatus(),
                proofUrl,
                proofUploadedAt
        );
    }

}
