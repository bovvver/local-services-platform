package com.github.bovvver;

import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;

class UserMapper {

    static User toDomain(UserEntity entity) {
        return User.create(
                UserId.of(entity.getId()),
                new Email(entity.getEmail()),
                entity.getFirstName(),
                entity.getLastName()
        );
    }

    static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId().value(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}
