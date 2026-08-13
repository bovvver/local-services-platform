package com.github.bovvver.badgemanagement;

import com.github.bovvver.vo.BadgeId;
import com.github.bovvver.vo.BadgeType;
import com.github.bovvver.vo.UserId;

import java.time.LocalDateTime;

/**
 * Badge aggregate — represents an achievement awarded to a provider.
 *
 * <p>One user may hold many badges. Badges may have an expiry date.
 * Awarded in response to {@code BookingCompleted} and {@code ReviewAdded} events.</p>
 */
public class Badge {

    private final BadgeId id;
    private final UserId userId;
    private final BadgeType type;
    private final LocalDateTime awardedAt;
    private final LocalDateTime expiresAt;

    Badge(final BadgeId id,
          final UserId userId,
          final BadgeType type,
          final LocalDateTime awardedAt,
          final LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.awardedAt = awardedAt;
        this.expiresAt = expiresAt;
    }

    /**
     * Factory — awards a non-expiring badge to a user.
     *
     * @param userId the recipient user's identifier
     * @param type   the badge type to award
     * @return a newly awarded badge
     */
    public static Badge award(UserId userId, BadgeType type) {
        return new Badge(BadgeId.generate(), userId, type, LocalDateTime.now(), null);
    }

    public BadgeId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public BadgeType getType() {
        return type;
    }

    public LocalDateTime getAwardedAt() {
        return awardedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
