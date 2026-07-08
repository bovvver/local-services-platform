package com.github.bovvver.profilemanagement;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlProviderProfileRepository extends Repository<ProviderProfileEntity, UUID> {

    ProviderProfileEntity save(ProviderProfileEntity entity);
}
