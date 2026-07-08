package com.github.bovvver.experiencemanagement;

import com.github.bovvver.vo.ExperienceLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * JPA entity for the {@code experience_snapshot} table.
 * The userId is both the primary key and a logical FK to users.id.
 */
@Entity
@Table(name = "experience_snapshot")
@Getter
@AllArgsConstructor
@NoArgsConstructor
class ExperienceSnapshotEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    private ExperienceLevel level;

    @Column(name = "score", nullable = false)
    private int score;
}
