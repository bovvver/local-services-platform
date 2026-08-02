package com.github.bovvver.reputationmanagement;

import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.UserId;

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
    private int completedBookings;
    private int cancelledBookings;

    Reputation(final UserId userId,
               final Rating averageRating,
               final int completedBookings,
               final int cancelledBookings) {
        this.userId = userId;
        this.averageRating = averageRating;
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
        return new Reputation(userId,  Rating.of(0.0), 0, 0);
    }

    public void incrementCancelledBookings() {
        this.cancelledBookings++;
    }

    void incrementCompletedBookings() {
        this.completedBookings++;
    }

    public UserId getUserId() {
        return userId;
    }

    public Rating getAverageRating() {
        return averageRating;
    }

    public int getCompletedBookings() {
        return completedBookings;
    }

    public int getCancelledBookings() {
        return cancelledBookings;
    }
}
