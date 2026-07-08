package com.github.bovvver.verificationmanagement;

import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationMapperTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void shouldMapEntityToDomain() {
        VerificationEntity entity = new VerificationEntity(
                USER_ID,
                VerificationStatus.VERIFIED
        );

        Verification verification = VerificationMapper.toDomain(entity);

        assertThat(verification.getUserId().value()).isEqualTo(USER_ID);
        assertThat(verification.getIdentityStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    void shouldMapDomainToEntity() {
        Verification verification = new Verification(
                UserId.of(USER_ID),
                VerificationStatus.VERIFIED
        );

        VerificationEntity entity = VerificationMapper.toEntity(verification);

        assertThat(entity.getUserId()).isEqualTo(USER_ID);
        assertThat(entity.getIdentityStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }
}
