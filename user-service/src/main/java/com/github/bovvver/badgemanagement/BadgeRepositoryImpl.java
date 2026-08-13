package com.github.bovvver.badgemanagement;

import com.github.bovvver.vo.BadgeType;
import com.github.bovvver.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
class BadgeRepositoryImpl implements BadgeRepository {

    private final SqlBadgeRepository repository;

    @Override
    public Badge save(final Badge badge) {
        BadgeEntity entity = repository.save(BadgeMapper.toEntity(badge));
        return BadgeMapper.toDomain(entity);
    }

    @Override
    public void saveAll(final List<Badge> badges) {
        List<BadgeEntity> entities = badges.stream().map(BadgeMapper::toEntity).collect(Collectors.toList());
        repository.saveAll(entities);
    }

    @Override
    public void deleteByUserIdAndType(final UserId userId, final BadgeType type) {
        repository.deleteByUserIdAndBadgeType(userId.value(), type);
    }
}
