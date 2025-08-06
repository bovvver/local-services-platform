package com.github.bovvver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class UserRepositoryImpl implements UserRepository {
    private final SqlUserRepository repository;

    @Override
    public User save(final User user) {
        UserEntity userEntity = repository.save(UserMapper.toEntity(user));
        UserEntity savedEntity = repository.save(userEntity);
        return UserMapper.toDomain(savedEntity);
    }
}
