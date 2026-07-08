package com.github.bovvver.reputationmanagement;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlReputationRepository extends Repository<ReputationEntity, UUID> {

    ReputationEntity save(ReputationEntity entity);
}
