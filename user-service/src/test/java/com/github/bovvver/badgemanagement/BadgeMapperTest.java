package com.github.bovvver.badgemanagement;

import com.github.bovvver.vo.BadgeId;
import com.github.bovvver.vo.BadgeType;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BadgeMapperTest {

    @Test
    void shouldMapEntityToDomain() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BadgeType type = BadgeType.TOP_RATED;
        LocalDateTime awardedAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(10);

        BadgeEntity entity = new BadgeEntity(id, userId, type, awardedAt, expiresAt);

        // Act
        Badge badge = BadgeMapper.toDomain(entity);

        // Assert
        assertThat(badge.getId().value()).isEqualTo(id);
        assertThat(badge.getUserId().value()).isEqualTo(userId);
        assertThat(badge.getType()).isEqualTo(type);
        assertThat(badge.getAwardedAt()).isEqualTo(awardedAt);
        assertThat(badge.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void shouldMapDomainToEntity() {
        // Arrange
        UserId userId = UserId.of(UUID.randomUUID());
        BadgeType type = BadgeType.RELIABLE;
        Badge badge = Badge.award(userId, type);

        // Act
        BadgeEntity entity = BadgeMapper.toEntity(badge);

        // Assert
        assertThat(entity.getId()).isEqualTo(badge.getId().value());
        assertThat(entity.getUserId()).isEqualTo(badge.getUserId().value());
        assertThat(entity.getBadgeType()).isEqualTo(badge.getType());
        assertThat(entity.getAwardedAt()).isEqualTo(badge.getAwardedAt());
        assertThat(entity.getExpiresAt()).isEqualTo(badge.getExpiresAt());
    }
}
