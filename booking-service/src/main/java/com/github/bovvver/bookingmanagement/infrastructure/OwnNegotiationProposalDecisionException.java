package com.github.bovvver.bookingmanagement.infrastructure;

/**
 * Thrown when a negotiation party attempts to accept/reject their own proposal.
 */
public class OwnNegotiationProposalDecisionException extends RuntimeException {

    public OwnNegotiationProposalDecisionException() {
        super("Cannot decide on own proposal");
    }
}

