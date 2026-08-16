package com.github.bovvver.usermanagement;

import com.github.bovvver.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class UserRepositoryImpl implements UserRepository {

    private final SqlUserRepository repository;

    @Override
    public User save(final User user) {
        UserEntity entity = repository.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(entity);
    }

    @Override
    public Optional<User> findById(final UserId id) {
        return repository.findById(id.value()).map(UserMapper::toDomain);
    }
}
