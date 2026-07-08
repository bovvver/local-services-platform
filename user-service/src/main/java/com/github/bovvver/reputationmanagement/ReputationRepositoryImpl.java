package com.github.bovvver.reputationmanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ReputationRepositoryImpl implements ReputationRepository {

    private final SqlReputationRepository repository;

    @Override
    public Reputation save(final Reputation reputation) {
        ReputationEntity entity = repository.save(ReputationMapper.toEntity(reputation));
        return ReputationMapper.toDomain(entity);
    }
}
