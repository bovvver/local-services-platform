package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.NegotiationId;
import com.github.bovvver.bookingmanagement.vo.NegotiationParty;
import com.github.bovvver.bookingmanagement.vo.NegotiationPositionId;
import com.github.bovvver.bookingmanagement.vo.Salary;

import java.time.LocalDateTime;

public class NegotiationPosition {

    private final NegotiationPositionId id;
    private final NegotiationId negotiationId;
    private final Salary proposedSalary;
    private final NegotiationParty proposedBy;
    private final LocalDateTime proposedAt;

    NegotiationPosition(final NegotiationPositionId id,
                        final NegotiationId negotiationId,
                        final Salary proposedSalary,
                        final NegotiationParty proposedBy,
                        final LocalDateTime proposedAt) {
        this.id = id;
        this.negotiationId = negotiationId;
        this.proposedSalary = proposedSalary;
        this.proposedBy = proposedBy;
        this.proposedAt = proposedAt;
    }

    NegotiationPosition(final NegotiationPositionId id,
                        final NegotiationId negotiationId,
                        final Salary proposedSalary,
                        final NegotiationParty proposedBy) {
        this(id, negotiationId, proposedSalary, proposedBy, LocalDateTime.now());
    }

    static NegotiationPosition create(
            final NegotiationPositionId id,
            final NegotiationId negotiationId,
            final Salary proposedSalary,
            final NegotiationParty proposedBy
    ) {
        return new NegotiationPosition(id, negotiationId, proposedSalary, proposedBy);
    }

    NegotiationPositionId getId() {
        return id;
    }

    NegotiationId getNegotiationId() {
        return negotiationId;
    }

    Salary getProposedSalary() {
        return proposedSalary;
    }

    NegotiationParty getProposedBy() {
        return proposedBy;
    }

    LocalDateTime getProposedAt() {
        return proposedAt;
    }
}
