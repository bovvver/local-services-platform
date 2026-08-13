package com.github.bovvver.badgemanagement;

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
class ReputationUpdatedBadgeListenerTest {

    @Mock
    private BadgeAssignmentService badgeAssignmentService;

    @InjectMocks
    private ReputationUpdatedBadgeListener listener;

    @Test
    void shouldForwardReputationUpdatedEventToAssignmentService() {
        // Arrange
        UserId userId = UserId.of(UUID.randomUUID());
        ReputationUpdated event = new ReputationUpdated(userId, Rating.of(4.7), 15, 1);

        // Act
        listener.handle(event);

        // Assert
        verify(badgeAssignmentService).addBadges(event);
    }
}
