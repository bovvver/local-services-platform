package com.github.bovvver.experiencemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReputationUpdatedListenerTest {

    @Mock
    private ExperienceModificationService experienceModificationService;

    @InjectMocks
    private ReputationUpdatedListener listener;

    @Test
    void shouldCallRecalculateExperienceWhenReputationUpdatedEventReceived() {
        UserId userId = UserId.of(UUID.randomUUID());
        Rating rating = Rating.of(4.5);
        ReputationUpdated event = new ReputationUpdated(userId, rating, 10, 2);

        listener.handle(event);

        verify(experienceModificationService).recalculateExperience(userId, rating, 10, 2);
    }
}
