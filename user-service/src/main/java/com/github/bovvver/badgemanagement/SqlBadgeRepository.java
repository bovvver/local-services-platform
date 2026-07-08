package com.github.bovvver.badgemanagement;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlBadgeRepository extends Repository<BadgeEntity, UUID> {

    BadgeEntity save(BadgeEntity entity);
}
