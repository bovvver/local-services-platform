package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Negotiation {

    private final NegotiationId id;
    private final BookingId bookingId;
    private final UserId offerAuthorId;
    private final List<NegotiationPosition> positions;
    private NegotiationStatus status;
    private final LocalDateTime startedAt;
    private LocalDateTime lastUpdatedAt;

    Negotiation(final NegotiationId id,
                final BookingId bookingId,
                final UserId offerAuthorId,
                final List<NegotiationPosition> positions,
                final NegotiationStatus status,
                final LocalDateTime startedAt,
                final LocalDateTime lastUpdatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.offerAuthorId = offerAuthorId;
        this.positions = positions;
        this.status = status;
        this.startedAt = startedAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    Negotiation(final NegotiationId id,
                final BookingId bookingId,
                final UserId offerAuthorId,
                final List<NegotiationPosition> positions) {
        this(id, bookingId, offerAuthorId, positions, NegotiationStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
    }

     static Negotiation create(
            final BookingId bookingId,
            final UserId offerAuthorId
    ) {
        return new Negotiation(NegotiationId.generate(), bookingId, offerAuthorId, new ArrayList<>());
    }

    void addPosition(Salary proposedSalary, final NegotiationParty proposedBy) {
        if(this.status != NegotiationStatus.ACTIVE) {
            throw new IllegalStateException("Cannot add position to a non-active negotiation.");
        }
        NegotiationPosition position = NegotiationPosition.create(this.id, proposedSalary, proposedBy);
        this.positions.add(position);
    }

    NegotiationId getId() {
        return id;
    }

    BookingId getBookingId() {
        return bookingId;
    }

    UserId getOfferAuthorId() {
        return offerAuthorId;
    }

    List<NegotiationPosition> getPositions() {
        return List.copyOf(positions);
    }

    NegotiationStatus getStatus() {
        return status;
    }

    LocalDateTime getStartedAt() {
        return startedAt;
    }

    LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }
}
