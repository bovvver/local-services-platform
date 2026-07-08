package com.github.bovvver.badgemanagement;

import com.github.bovvver.vo.BadgeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity for the {@code badges} table.
 */
@Entity
@Table(name = "badges")
@Getter
@AllArgsConstructor
@NoArgsConstructor
class BadgeEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false, length = 30)
    private BadgeType badgeType;

    @Column(name = "awarded_at", nullable = false)
    private LocalDateTime awardedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
