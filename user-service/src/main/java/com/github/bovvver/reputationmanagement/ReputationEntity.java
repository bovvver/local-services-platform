package com.github.bovvver.reputationmanagement;

import com.github.bovvver.vo.ExperienceLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * JPA entity for the {@code reputation} table.
 * The userId is both the primary key and a logical FK to users.id.
 */
@Entity
@Table(name = "reputation")
@Getter
@AllArgsConstructor
@NoArgsConstructor
class ReputationEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "average_rating", nullable = false)
    private double averageRating;

    @Column(name = "total_ratings", nullable = false)
    private int totalRatings;

    @Column(name = "completed_bookings", nullable = false)
    private int completedBookings;

    @Column(name = "cancelled_bookings", nullable = false)
    private int cancelledBookings;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false, length = 20)
    private ExperienceLevel experienceLevel;

    @Column(name = "experience_score", nullable = false)
    private int experienceScore;
}
