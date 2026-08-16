package com.github.bovvver.reputationmanagement;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.vo.ExperienceLevel;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.Score;
import com.github.bovvver.vo.UserId;

import java.util.ArrayList;
import java.util.List;

/**
 * Reputation aggregate — a projection of a user's reputation based on
 * completed bookings, cancellations, average rating, and experience level.
 */
public class Reputation {

    private final UserId userId;
    private Rating averageRating;
    private int totalRatings;
    private int completedBookings;
    private int cancelledBookings;
    private ExperienceLevel experienceLevel;
    private Score experienceScore;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    Reputation(final UserId userId,
               final Rating averageRating,
               final int totalRatings,
               final int completedBookings,
               final int cancelledBookings,
               final ExperienceLevel experienceLevel,
               final Score experienceScore) {
        this.userId = userId;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
        this.experienceLevel = experienceLevel;
        this.experienceScore = experienceScore;
    }

    /**
     * Factory — creates a zeroed reputation with BEGINNER level for a new user.
     *
     * @param userId the owning user's identifier
     * @return a new {@code Reputation} with all counters at zero
     */
    public static Reputation initialize(UserId userId) {
        return new Reputation(userId, Rating.of(0.0), 0, 0, 0, ExperienceLevel.BEGINNER, Score.of(0));
    }

    public void addRating(Rating newRating) {
        double currentSum = averageRating.value() * totalRatings;
        totalRatings++;
        double newAverage = (currentSum + newRating.value()) / totalRatings;

        averageRating = Rating.newAverage(newAverage);
        recalculateExperience();
        domainEvents.add(new ReputationUpdated(userId, averageRating, completedBookings, cancelledBookings));
    }

    public void incrementCancelledBookings() {
        this.cancelledBookings++;
        recalculateExperience();
        domainEvents.add(new ReputationUpdated(userId, averageRating, completedBookings, cancelledBookings));
    }

    void incrementCompletedBookings() {
        this.completedBookings++;
        recalculateExperience();
        domainEvents.add(new ReputationUpdated(userId, averageRating, completedBookings, cancelledBookings));
    }

    private void recalculateExperience() {
        double scoreValue = (completedBookings * 100.0) * (averageRating.value() / 5.0) - (cancelledBookings * 200.0);
        int finalScore = Math.max(0, (int) Math.round(scoreValue));

        this.experienceScore = Score.of(finalScore);
        this.experienceLevel = ExperienceLevel.fromScore(finalScore);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public UserId getUserId() {
        return userId;
    }

    public Rating getAverageRating() {
        return averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public int getCompletedBookings() {
        return completedBookings;
    }

    public int getCancelledBookings() {
        return cancelledBookings;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public Score getExperienceScore() {
        return experienceScore;
    }
}
