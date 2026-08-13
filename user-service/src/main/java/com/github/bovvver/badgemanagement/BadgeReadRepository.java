package com.github.bovvver.badgemanagement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface BadgeReadRepository extends JpaRepository<BadgeEntity, UUID> {

    List<BadgeEntity> findAllByUserId(UUID userId);
}
