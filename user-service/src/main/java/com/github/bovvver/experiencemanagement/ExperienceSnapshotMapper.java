package com.github.bovvver.experiencemanagement;

import com.github.bovvver.vo.Score;
import com.github.bovvver.vo.UserId;

/**
 * Manual mapper between {@link ExperienceSnapshot} and {@link ExperienceSnapshotEntity}.
 */
class ExperienceSnapshotMapper {

    static ExperienceSnapshot toDomain(ExperienceSnapshotEntity entity) {
        return new ExperienceSnapshot(
                UserId.of(entity.getUserId()),
                entity.getLevel(),
                Score.of(entity.getScore())
        );
    }

    static ExperienceSnapshotEntity toEntity(ExperienceSnapshot snapshot) {
        return new ExperienceSnapshotEntity(
                snapshot.getUserId().value(),
                snapshot.getLevel(),
                snapshot.getScore().value()
        );
    }
}
