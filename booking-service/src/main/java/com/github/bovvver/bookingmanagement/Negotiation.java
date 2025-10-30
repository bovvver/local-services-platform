package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.NegotiationId;
import com.github.bovvver.bookingmanagement.vo.NegotiationStatus;

import java.time.LocalDateTime;
import java.util.List;

public class Negotiation {

    private final NegotiationId id;
    private final BookingId bookingId;
    private final List<NegotiationPosition> positions;
    private NegotiationStatus status;
    private final LocalDateTime startedAt;
    private LocalDateTime lastUpdatedAt;

    Negotiation(final NegotiationId id,
                final BookingId bookingId,
                final List<NegotiationPosition> positions,
                final NegotiationStatus status,
                final LocalDateTime startedAt,
                final LocalDateTime lastUpdatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.positions = positions;
        this.status = status;
        this.startedAt = startedAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    Negotiation(final NegotiationId id,
                final BookingId bookingId,
                final List<NegotiationPosition> positions) {

        this(id, bookingId, positions, NegotiationStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
    }

    static Negotiation create(
            final NegotiationId id,
            final BookingId bookingId,
            final List<NegotiationPosition> positions
    ) {
        return new Negotiation(id, bookingId, positions);
    }

    NegotiationId getId() {
        return id;
    }

    BookingId getBookingId() {
        return bookingId;
    }

    List<NegotiationPosition> getPositions() {
        return positions;
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
