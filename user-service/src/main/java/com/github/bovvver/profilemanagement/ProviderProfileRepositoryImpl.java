package com.github.bovvver.profilemanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ProviderProfileRepositoryImpl implements ProviderProfileRepository {

    private final SqlProviderProfileRepository repository;

    @Override
    public ProviderProfile save(final ProviderProfile profile) {
        ProviderProfileEntity entity = repository.save(ProviderProfileMapper.toEntity(profile));
        return ProviderProfileMapper.toDomain(entity);
    }
}
