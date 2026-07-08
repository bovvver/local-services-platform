package com.github.bovvver.badgemanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class BadgeRepositoryImpl implements BadgeRepository {

    private final SqlBadgeRepository repository;

    @Override
    public Badge save(final Badge badge) {
        BadgeEntity entity = repository.save(BadgeMapper.toEntity(badge));
        return BadgeMapper.toDomain(entity);
    }
}
