package com.github.bovvver.usermanagement;

import com.github.bovvver.usermanagement.verification.VerificationProof;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserMapperTest {

    private static final UUID TEST_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Test
    void shouldMapEntityToDomain() {
        LocalDateTime time = LocalDateTime.now();
        UserEntity userEntity = new UserEntity(
                TEST_UUID,
                "john@doesnot.exist",
                "John",
                "Doe",
                VerificationStatus.VERIFIED,
                "http://example.com/proof",
                time
        );
        User user = UserMapper.toDomain(userEntity);

        assertThat(user.getId().value()).isEqualTo(TEST_UUID);
        assertThat(user.getEmail().value()).isEqualTo("john@doesnot.exist");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getIdentityStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(user.getVerificationProof()).isNotNull();
        assertThat(user.getVerificationProof().url()).isEqualTo("http://example.com/proof");
        assertThat(user.getVerificationProof().uploadedAt()).isEqualTo(time);
    }

    @Test
    void shouldMapDomainToEntity() {
        User domainUser = User.create(
                UserId.of(TEST_UUID),
                new Email("john@doesnot.exist"),
                "John",
                "Doe"
        );
        domainUser.addVerificationProof(VerificationProof.of("http://example.com/proof"));
        domainUser.verify();

        UserEntity userEntity = UserMapper.toEntity(domainUser);

        assertThat(userEntity.getId()).isEqualTo(TEST_UUID);
        assertThat(userEntity.getEmail()).isEqualTo("john@doesnot.exist");
        assertThat(userEntity.getFirstName()).isEqualTo("John");
        assertThat(userEntity.getLastName()).isEqualTo("Doe");
        assertThat(userEntity.getIdentityStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(userEntity.getProofUrl()).isEqualTo("http://example.com/proof");
        assertThat(userEntity.getProofUploadedAt()).isNotNull();
    }
}
