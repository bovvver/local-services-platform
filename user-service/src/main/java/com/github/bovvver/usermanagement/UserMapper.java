package com.github.bovvver.usermanagement;

import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;

/**
 * Manual mapper between the {@link User} domain aggregate and the {@link UserEntity} JPA entity.
 */
class UserMapper {

    /**
     * Maps a {@link UserEntity} to the {@link User} domain aggregate.
     *
     * @param entity the JPA entity to map
     * @return the domain aggregate
     */
    static User toDomain(UserEntity entity) {
        return new User(
                UserId.of(entity.getId()),
                Email.of(entity.getEmail()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getStatus()
        );
    }

    /**
     * Maps a {@link User} domain aggregate to a {@link UserEntity} for persistence.
     *
     * @param user the domain aggregate to map
     * @return the JPA entity
     */
    static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId().value(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus()
        );
    }
}
