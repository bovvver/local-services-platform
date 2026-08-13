package com.github.bovvver.badgemanagement;

import com.github.bovvver.reputationmanagement.ReputationUpdated;
import com.github.bovvver.vo.BadgeType;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BadgeHandlingDomainServiceTest {

    private BadgeHandlingDomainService domainService;
    private UserId userId;

    @BeforeEach
    void setUp() {
        domainService = new BadgeHandlingDomainService(List.of(
                new TopRatedBadgeRule(),
                new ReliableBadgeRule()
        ));
        userId = UserId.of(UUID.randomUUID());
    }

    @Test
    void shouldAwardTopRatedBadgeWhenQualifiesAndDoesNotHaveIt() {
        // Arrange: Rating >= 4.5 and completed bookings >= 10
        ReputationUpdated event = new ReputationUpdated(userId, Rating.of(4.8), 10, 0);
        List<BadgeType> currentBadges = List.of();

        // Act
        BadgeEvaluationResult result = domainService.handleReputationBadges(event, currentBadges);

        // Assert
        assertThat(result.badgesToAward()).containsExactly(BadgeType.TOP_RATED);
        assertThat(result.badgesToRevoke()).isEmpty();
    }

    @Test
    void shouldRevokeTopRatedBadgeWhenRatingDropsBelowThreshold() {
        // Arrange: Rating < 4.5 but has 10 completed bookings, previously held TOP_RATED
        ReputationUpdated event = new ReputationUpdated(userId, Rating.of(4.2), 10, 0);
        List<BadgeType> currentBadges = List.of(BadgeType.TOP_RATED);

        // Act
        BadgeEvaluationResult result = domainService.handleReputationBadges(event, currentBadges);

        // Assert
        assertThat(result.badgesToAward()).isEmpty();
        assertThat(result.badgesToRevoke()).containsExactly(BadgeType.TOP_RATED);
    }

    @Test
    void shouldAwardReliableBadgeWhenEnoughBookingsAndLowCancellationRate() {
        // Arrange: 20 completed, 0 cancelled -> qualifies for RELIABLE
        ReputationUpdated event = new ReputationUpdated(userId, Rating.of(4.0), 20, 0);
        List<BadgeType> currentBadges = List.of();

        // Act
        BadgeEvaluationResult result = domainService.handleReputationBadges(event, currentBadges);

        // Assert
        assertThat(result.badgesToAward()).containsExactly(BadgeType.RELIABLE);
        assertThat(result.badgesToRevoke()).isEmpty();
    }

    @Test
    void shouldRevokeReliableBadgeWhenCancellationsExceedTenPercent() {
        // Arrange: 17 completed, 3 cancelled (3 / 20 = 15% cancellation rate) -> no longer reliable
        ReputationUpdated event = new ReputationUpdated(userId, Rating.of(4.0), 17, 3);
        List<BadgeType> currentBadges = List.of(BadgeType.RELIABLE);

        // Act
        BadgeEvaluationResult result = domainService.handleReputationBadges(event, currentBadges);

        // Assert
        assertThat(result.badgesToAward()).isEmpty();
        assertThat(result.badgesToRevoke()).containsExactly(BadgeType.RELIABLE);
    }

    @Test
    void shouldDoNothingWhenStatusMatchesQualifications() {
        // Arrange: Rating 4.8, 10 completed -> qualifies for TOP_RATED, already has it
        ReputationUpdated event = new ReputationUpdated(userId, Rating.of(4.8), 10, 0);
        List<BadgeType> currentBadges = List.of(BadgeType.TOP_RATED);

        // Act
        BadgeEvaluationResult result = domainService.handleReputationBadges(event, currentBadges);

        // Assert
        assertThat(result.badgesToAward()).isEmpty();
        assertThat(result.badgesToRevoke()).isEmpty();
    }
}
