package com.github.bovvver.experiencemanagement;

import com.github.bovvver.vo.ExperienceLevel;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExperienceModificationServiceTest {

    @Mock
    private ExperienceSnapshotReadRepository experienceReadRepository;

    @Mock
    private ExperienceSnapshotRepository experienceWriteRepository;

    @InjectMocks
    private ExperienceModificationService service;

    @Test
    void shouldLoadRecalculateAndSaveExperienceSnapshot() {
        UUID rawUserId = UUID.randomUUID();
        UserId userId = UserId.of(rawUserId);
        Rating rating = Rating.of(5.0);

        ExperienceSnapshotEntity entity = new ExperienceSnapshotEntity(
                rawUserId,
                ExperienceLevel.BEGINNER,
                0
        );

        when(experienceReadRepository.findByUserId(rawUserId)).thenReturn(Optional.of(entity));
        when(experienceWriteRepository.save(any(ExperienceSnapshot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.recalculateExperience(userId, rating, 10, 0);

        verify(experienceReadRepository).findByUserId(rawUserId);
        verify(experienceWriteRepository).save(any(ExperienceSnapshot.class));
    }
}
