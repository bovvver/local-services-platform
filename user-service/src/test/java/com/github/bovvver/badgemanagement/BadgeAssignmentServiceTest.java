package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import com.github.bovvver.vo.BadgeType;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeAssignmentServiceTest {

    @Mock
    private BadgeReadRepository badgeReadRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private BadgeHandlingDomainService badgeHandlingDomainService;

    @InjectMocks
    private BadgeAssignmentService badgeAssignmentService;

    @Test
    @SuppressWarnings("unchecked")
    void shouldOrchestrateBadgeAssignmentProcess() {
        // Arrange
        UserId userId = UserId.of(UUID.randomUUID());
        ReputationUpdated event = new ReputationUpdated(userId, Rating.of(4.8), 10, 0);

        BadgeEntity existingEntity = new BadgeEntity(
                UUID.randomUUID(),
                userId.value(),
                BadgeType.RELIABLE,
                LocalDateTime.now(),
                null
        );
        when(badgeReadRepository.findAllByUserId(userId.value())).thenReturn(List.of(existingEntity));

        BadgeEvaluationResult evaluationResult = new BadgeEvaluationResult(
                List.of(BadgeType.TOP_RATED),
                List.of(BadgeType.RELIABLE)
        );
        when(badgeHandlingDomainService.handleReputationBadges(eq(event), any(List.class))).thenReturn(evaluationResult);

        // Act
        badgeAssignmentService.addBadges(event);

        // Assert
        verify(badgeReadRepository).findAllByUserId(userId.value());
        verify(badgeHandlingDomainService).handleReputationBadges(eq(event), eq(List.of(BadgeType.RELIABLE)));
        verify(badgeRepository).deleteByUserIdAndType(userId, BadgeType.RELIABLE);

        ArgumentCaptor<List<Badge>> saveCaptor = ArgumentCaptor.forClass(List.class);
        verify(badgeRepository).saveAll(saveCaptor.capture());
        
        List<Badge> savedBadges = saveCaptor.getValue();
        assertThat(savedBadges).hasSize(1);
        assertThat(savedBadges.get(0).getUserId()).isEqualTo(userId);
        assertThat(savedBadges.get(0).getType()).isEqualTo(BadgeType.TOP_RATED);
    }
}
