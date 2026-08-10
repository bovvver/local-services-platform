package com.github.bovvver.experiencemanagement;

import com.github.bovvver.infrastructure.UserNotFoundException;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ExperienceModificationService {

    private final ExperienceSnapshotReadRepository experienceReadRepository;
    private final ExperienceSnapshotRepository experienceWriteRepository;

    @Transactional
    void recalculateExperience(final UserId userId, final Rating rating, final int completedBookings, final int cancelledBookings) {
        ExperienceSnapshotEntity experienceSnapshotEntity = experienceReadRepository.findByUserId(userId.value())
                .orElseThrow(() -> new UserNotFoundException("Experience snapshot not found for user: " + userId));

        ExperienceSnapshot experienceSnapshot = ExperienceSnapshotMapper.toDomain(experienceSnapshotEntity);
        experienceSnapshot.recalculateExperience(rating, completedBookings, cancelledBookings);

        experienceWriteRepository.save(experienceSnapshot);
    }
}
