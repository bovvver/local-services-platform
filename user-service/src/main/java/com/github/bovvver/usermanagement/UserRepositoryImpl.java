package com.github.bovvver.usermanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class UserRepositoryImpl implements UserRepository {
    private final SqlUserRepository repository;

    @Override
    public User save(final User user) {
        UserEntity userEntity = repository.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(userEntity);
    }
}
