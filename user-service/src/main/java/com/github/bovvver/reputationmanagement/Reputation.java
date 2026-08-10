package com.github.bovvver.reputationmanagement;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.UserId;

import java.util.ArrayList;
import java.util.List;

/**
 * Reputation aggregate — a projection of a user's reputation based on
 * completed bookings, cancellations, and average rating.
 *
 * <p>Updated in response to {@code BookingCompleted} and {@code ReviewAdded} events
 * from booking-service (to be implemented when those consumers are added).</p>
 */
public class Reputation {

    private final UserId userId;
    private Rating averageRating;
    private int totalRatings;
    private int completedBookings;
    private int cancelledBookings;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    Reputation(final UserId userId,
               final Rating averageRating,
               final int totalRatings,
               final int completedBookings,
               final int cancelledBookings) {
        this.userId = userId;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
    }

    /**
     * Factory — creates a zeroed reputation for a new user.
     *
     * @param userId the owning user's identifier
     * @return a new {@code Reputation} with all counters at zero
     */
    public static Reputation initialize(UserId userId) {
        return new Reputation(userId,  Rating.of(0.0), 0, 0, 0);
    }

    public void addRating(Rating newRating) {
        double currentSum = averageRating.value() * totalRatings;
        totalRatings++;
        double newAverage = (currentSum + newRating.value()) / totalRatings;

        averageRating = Rating.newAverage(newAverage);
        domainEvents.add(new ReputationUpdated(userId, averageRating));
    }

    public void incrementCancelledBookings() {
        this.cancelledBookings++;
    }

    void incrementCompletedBookings() {
        this.completedBookings++;
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
}
