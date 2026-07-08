package com.github.bovvver.badgemanagement;

import com.github.bovvver.vo.BadgeId;
import com.github.bovvver.vo.UserId;

/**
 * Manual mapper between {@link Badge} and {@link BadgeEntity}.
 */
class BadgeMapper {

    static Badge toDomain(BadgeEntity entity) {
        return new Badge(
                BadgeId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                entity.getBadgeType(),
                entity.getAwardedAt(),
                entity.getExpiresAt()
        );
    }

    static BadgeEntity toEntity(Badge badge) {
        return new BadgeEntity(
                badge.getId().value(),
                badge.getUserId().value(),
                badge.getType(),
                badge.getAwardedAt(),
                badge.getExpiresAt()
        );
    }
}
