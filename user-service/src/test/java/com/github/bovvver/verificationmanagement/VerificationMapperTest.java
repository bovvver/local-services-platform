package com.github.bovvver.verificationmanagement;

import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VerificationMapperTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @ParameterizedTest
    @EnumSource(VerificationStatus.class)
    void shouldMapEntityToDomainWithoutProofForAllStatuses(VerificationStatus status) {
        VerificationEntity entity = new VerificationEntity(
                USER_ID,
                status,
                null,
                null
        );

        Verification verification = VerificationMapper.toDomain(entity);

        assertThat(verification.getUserId().value()).isEqualTo(USER_ID);
        assertThat(verification.getIdentityStatus()).isEqualTo(status);
        assertThat(verification.getVerificationProof()).isNull();
    }

    @Test
    void shouldMapEntityToDomainWithProof() {
        LocalDateTime now = LocalDateTime.now();
        VerificationEntity entity = new VerificationEntity(
                USER_ID,
                VerificationStatus.VERIFIED,
                "http://example.com/proof",
                now
        );

        Verification verification = VerificationMapper.toDomain(entity);

        assertThat(verification.getUserId().value()).isEqualTo(USER_ID);
        assertThat(verification.getIdentityStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(verification.getVerificationProof()).isNotNull();
        assertThat(verification.getVerificationProof().url()).isEqualTo("http://example.com/proof");
        assertThat(verification.getVerificationProof().uploadedAt()).isEqualTo(now);
    }

    @ParameterizedTest
    @EnumSource(VerificationStatus.class)
    void shouldMapDomainToEntityWithoutProofForAllStatuses(VerificationStatus status) {
        Verification verification = new Verification(
                UserId.of(USER_ID),
                status,
                null
        );

        VerificationEntity entity = VerificationMapper.toEntity(verification);

        assertThat(entity.getUserId()).isEqualTo(USER_ID);
        assertThat(entity.getIdentityStatus()).isEqualTo(status);
        assertThat(entity.getProofUrl()).isNull();
        assertThat(entity.getProofUploadedAt()).isNull();
    }

    @Test
    void shouldMapDomainToEntityWithProof() {
        LocalDateTime now = LocalDateTime.now();
        Verification verification = new Verification(
                UserId.of(USER_ID),
                VerificationStatus.VERIFIED,
                new VerificationProof("http://example.com/proof", now)
        );

        VerificationEntity entity = VerificationMapper.toEntity(verification);

        assertThat(entity.getUserId()).isEqualTo(USER_ID);
        assertThat(entity.getIdentityStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(entity.getProofUrl()).isEqualTo("http://example.com/proof");
        assertThat(entity.getProofUploadedAt()).isEqualTo(now);
    }

    @Test
    void shouldThrowExceptionWhenMappingNullEntity() {
        assertThrows(NullPointerException.class, () -> VerificationMapper.toDomain(null));
    }

    @Test
    void shouldThrowExceptionWhenMappingNullDomain() {
        assertThrows(NullPointerException.class, () -> VerificationMapper.toEntity(null));
    }
}
