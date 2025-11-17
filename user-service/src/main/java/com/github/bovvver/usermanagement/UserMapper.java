package com.github.bovvver.usermanagement;

import com.github.bovvver.usermanagement.vo.*;

class UserMapper {

    /**
     * Maps a UserEntity object to a User domain object.
     *
     * @param entity the UserEntity object to be mapped
     * @return a User domain object containing the mapped data
     */
    static User toDomain(UserEntity entity) {
        return new User(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail()),
                entity.getFirstName(),
                entity.getLastName(),
                City.of(entity.getCity()),
                Country.of(entity.getCountry()),
                entity.getExperienceLevel(),
                entity.getServiceCategories(),
                entity.getAwardTags(),
                entity.getStatus(),
                OfferId.fromAll(entity.getMyOfferIds()),
                OfferId.fromAll(entity.getAssignedOfferIds()),
                BookingId.fromAll(entity.getSentBookingIds())
        );
    }

    /**
     * Maps a User domain object to a UserEntity database entity.
     *
     * @param user the User domain object to be mapped
     * @return a UserEntity object containing the mapped data
     */
    static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId().value(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName(),
                user.getCity() == null ? null : user.getCity().value(),
                user.getCountry() == null ? null : user.getCountry().code(),
                user.getExperienceLevel(),
                user.getServiceCategories(),
                user.getAwardTags(),
                user.getStatus(),
                user.getMyOfferIds().stream().map(OfferId::value).toList(),
                user.getAssignedOfferIds().stream().map(OfferId::value).toList(),
                user.getSentBookingIds().stream().map(BookingId::value).toList()
        );
    }
}
