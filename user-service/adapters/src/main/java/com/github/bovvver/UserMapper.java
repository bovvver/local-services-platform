package com.github.bovvver;

import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.Location;
import com.github.bovvver.vo.UserId;

class UserMapper {

    static User toDomain(UserEntity entity) {
        return User.create(
                UserId.of(entity.getId()),
                new Email(entity.getEmail()),
                entity.getFirstName(),
                entity.getLastName(),
                Location.of(entity.getLocation().getLatitude(), entity.getLocation().getLongitude())
        );
    }

    static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId().value(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName(),
                new LocationEmbeddable(user.getLocation().latitude(), user.getLocation().longitude())
        );
    }
}
