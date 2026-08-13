package com.github.bovvver.badgemanagement;

import com.github.bovvver.vo.BadgeType;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

interface SqlBadgeRepository extends Repository<BadgeEntity, UUID> {

    BadgeEntity save(BadgeEntity entity);

    void saveAll(List<BadgeEntity> entities);

    void deleteByUserIdAndBadgeType(UUID userId, BadgeType type);
}
