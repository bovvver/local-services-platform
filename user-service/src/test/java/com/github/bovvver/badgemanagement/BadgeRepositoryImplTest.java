package com.github.bovvver.badgemanagement;

import com.github.bovvver.vo.BadgeType;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeRepositoryImplTest {

    @Mock
    private SqlBadgeRepository sqlBadgeRepository;

    @InjectMocks
    private BadgeRepositoryImpl badgeRepository;

    @Test
    void shouldSaveBadge() {
        // Arrange
        UserId userId = UserId.of(UUID.randomUUID());
        Badge badge = Badge.award(userId, BadgeType.TOP_RATED);

        BadgeEntity mappedEntity = BadgeMapper.toEntity(badge);
        when(sqlBadgeRepository.save(any(BadgeEntity.class))).thenReturn(mappedEntity);

        // Act
        Badge savedBadge = badgeRepository.save(badge);

        // Assert
        verify(sqlBadgeRepository).save(any(BadgeEntity.class));
        assertThat(savedBadge.getId()).isEqualTo(badge.getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSaveAllBadges() {
        // Arrange
        UserId userId = UserId.of(UUID.randomUUID());
        Badge badge1 = Badge.award(userId, BadgeType.TOP_RATED);
        Badge badge2 = Badge.award(userId, BadgeType.RELIABLE);

        // Act
        badgeRepository.saveAll(List.of(badge1, badge2));

        // Assert
        ArgumentCaptor<List<BadgeEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(sqlBadgeRepository).saveAll(captor.capture());
        
        List<BadgeEntity> savedEntities = captor.getValue();
        assertThat(savedEntities).hasSize(2);
        assertThat(savedEntities.get(0).getBadgeType()).isEqualTo(BadgeType.TOP_RATED);
        assertThat(savedEntities.get(1).getBadgeType()).isEqualTo(BadgeType.RELIABLE);
    }

    @Test
    void shouldDeleteByUserIdAndType() {
        // Arrange
        UserId userId = UserId.of(UUID.randomUUID());
        BadgeType type = BadgeType.RELIABLE;

        // Act
        badgeRepository.deleteByUserIdAndType(userId, type);

        // Assert
        verify(sqlBadgeRepository).deleteByUserIdAndBadgeType(userId.value(), type);
    }
}
