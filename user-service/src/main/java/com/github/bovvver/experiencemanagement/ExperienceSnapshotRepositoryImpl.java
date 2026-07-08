package com.github.bovvver.experiencemanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ExperienceSnapshotRepositoryImpl implements ExperienceSnapshotRepository {

    private final SqlExperienceSnapshotRepository repository;

    @Override
    public ExperienceSnapshot save(final ExperienceSnapshot snapshot) {
        ExperienceSnapshotEntity entity = repository.save(ExperienceSnapshotMapper.toEntity(snapshot));
        return ExperienceSnapshotMapper.toDomain(entity);
    }
}
