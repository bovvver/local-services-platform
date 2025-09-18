package com.github.bovvver.usermanagement;

import com.github.bovvver.usermanagement.vo.Email;
import com.github.bovvver.usermanagement.vo.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserMapperTest {

    private static final UUID TEST_UUID = UUID.fromString( "123e4567-e89b-12d3-a456-426614174000");

    @Test
    void shouldMapEntityToDomain()  {
        UserEntity userEntity = new UserEntity(
                TEST_UUID,
                "john@doesnot.exist",
                "John",
                "Doe"
        );
        User user = UserMapper.toDomain(userEntity);

        assertThat(user.getId().value()).isEqualTo(TEST_UUID);
        assertThat(user.getEmail().value()).isEqualTo("john@doesnot.exist");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldMapDomainToEntity()  {
        User domainUser = User.create(
                UserId.of(TEST_UUID),
                new Email("john@doesnot.exist"),
                "John",
                "Doe"
        );
        UserEntity userEntity = UserMapper.toEntity(domainUser);

        assertThat(userEntity.getId()).isEqualTo(TEST_UUID);
        assertThat(userEntity.getEmail()).isEqualTo("john@doesnot.exist");
        assertThat(userEntity.getFirstName()).isEqualTo("John");
        assertThat(userEntity.getLastName()).isEqualTo("Doe");
    }
}
