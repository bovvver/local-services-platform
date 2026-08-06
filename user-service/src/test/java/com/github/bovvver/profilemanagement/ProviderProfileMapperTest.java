package com.github.bovvver.profilemanagement;

import com.github.bovvver.vo.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProviderProfileMapperTest {

    private static final UUID PROFILE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void shouldMapEntityToDomain() {
        ProviderProfileEntity entity = new ProviderProfileEntity(
                PROFILE_ID,
                USER_ID,
                "Sample Bio",
                "New York",
                "US",
                Set.of(ServiceCategory.CLEANING)
        );

        ProviderProfile profile = ProviderProfileMapper.toDomain(entity);

        assertThat(profile.getId().value()).isEqualTo(PROFILE_ID);
        assertThat(profile.getUserId().value()).isEqualTo(USER_ID);
        assertThat(profile.getBio()).isEqualTo(Bio.of("Sample Bio"));
        assertThat(profile.getCity().value()).isEqualTo("New York");
        assertThat(profile.getCountry().code()).isEqualTo("US");
        assertThat(profile.getCategories()).containsExactly(ServiceCategory.CLEANING);
    }

    @Test
    void shouldMapDomainToEntity() {
        ProviderProfile profile = new ProviderProfile(
                ProviderProfileId.of(PROFILE_ID),
                UserId.of(USER_ID),
                Bio.of("Sample Bio"),
                City.of("New York"),
                Country.of("US"),
                Set.of(ServiceCategory.CLEANING)
        );

        ProviderProfileEntity entity = ProviderProfileMapper.toEntity(profile);

        assertThat(entity.getId()).isEqualTo(PROFILE_ID);
        assertThat(entity.getUserId()).isEqualTo(USER_ID);
        assertThat(entity.getBio()).isEqualTo("Sample Bio");
        assertThat(entity.getCity()).isEqualTo("New York");
        assertThat(entity.getCountry()).isEqualTo("US");
        assertThat(entity.getCategories()).containsExactly(ServiceCategory.CLEANING);
    }

    @Test
    void shouldMapNullsGracefully() {
        ProviderProfile profile = new ProviderProfile(
                ProviderProfileId.of(PROFILE_ID),
                UserId.of(USER_ID),
                null,
                null,
                null,
                Set.of()
        );

        ProviderProfileEntity entity = ProviderProfileMapper.toEntity(profile);

        assertThat(entity.getId()).isEqualTo(PROFILE_ID);
        assertThat(entity.getUserId()).isEqualTo(USER_ID);
        assertThat(entity.getBio()).isNull();
        assertThat(entity.getCity()).isNull();
        assertThat(entity.getCountry()).isNull();
        assertThat(entity.getCategories()).isEmpty();
    }
}
