package com.github.bovvver.badgemanagement;

import com.github.bovvver.vo.BadgeType;
import com.github.bovvver.vo.UserId;

import java.util.List;

/**
 * Write-side (command) repository for the {@link Badge} aggregate.
 * Contains only state-mutating operations.
 */
public interface BadgeRepository {

    Badge save(Badge badge);

    void saveAll(List<Badge> badges);

    void deleteByUserIdAndType(UserId userId, BadgeType type);

}
