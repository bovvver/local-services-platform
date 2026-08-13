package com.github.bovvver.badgemanagement;

import com.github.bovvver.vo.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface BadgeReadRepository extends JpaRepository<BadgeEntity, UUID> {

    Optional<BadgeEntity> findByUserId(UUID userId);

    boolean existsByUserIdAndBadgeType(UUID userId, BadgeType type);
}
