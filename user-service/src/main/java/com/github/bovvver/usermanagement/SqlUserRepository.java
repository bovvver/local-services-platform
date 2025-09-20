package com.github.bovvver.usermanagement;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlUserRepository extends Repository<UserEntity, UUID> {

    UserEntity save(UserEntity entity);
}
