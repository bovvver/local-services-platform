package com.github.bovvver.experiencemanagement;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlExperienceSnapshotRepository extends Repository<ExperienceSnapshotEntity, UUID> {

    ExperienceSnapshotEntity save(ExperienceSnapshotEntity entity);
}
