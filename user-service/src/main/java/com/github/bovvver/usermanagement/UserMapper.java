package com.github.bovvver.usermanagement;

import com.github.bovvver.usermanagement.vo.Email;
import com.github.bovvver.usermanagement.vo.UserId;

class UserMapper {

    /**
     * Maps a UserEntity object to a User domain object.
     *
     * @param entity the UserEntity object to be mapped
     * @return a User domain object containing the mapped data
     */
    static User toDomain(UserEntity entity) {
        return User.create(
                UserId.of(entity.getId()),
                new Email(entity.getEmail()),
                entity.getFirstName(),
                entity.getLastName()
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
                user.getLastName()
        );
    }
}
