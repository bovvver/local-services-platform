package com.github.bovvver.profilemanagement;

import com.github.bovvver.vo.*;

/**
 * Manual mapper between {@link ProviderProfile} and {@link ProviderProfileEntity}.
 */
class ProviderProfileMapper {

    /**
     * Maps a JPA entity to the domain aggregate.
     *
     * @param entity the JPA entity
     * @return domain aggregate
     */
    static ProviderProfile toDomain(ProviderProfileEntity entity) {
        return new ProviderProfile(
                ProviderProfileId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                entity.getBio(),
                City.of(entity.getCity()),
                Country.of(entity.getCountry()),
                entity.getCategories()
        );
    }

    /**
     * Maps a domain aggregate to the JPA entity.
     *
     * @param profile the domain aggregate
     * @return JPA entity
     */
    static ProviderProfileEntity toEntity(ProviderProfile profile) {
        return new ProviderProfileEntity(
                profile.getId().value(),
                profile.getUserId().value(),
                profile.getBio(),
                profile.getCity() == null ? null : profile.getCity().value(),
                profile.getCountry() == null ? null : profile.getCountry().code(),
                profile.getCategories()
        );
    }
}
