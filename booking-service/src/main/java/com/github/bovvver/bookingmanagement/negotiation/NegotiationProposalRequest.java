package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.vo.NegotiationParty;

import java.math.BigDecimal;

record NegotiationProposalRequest(
        NegotiationParty proposedBy,
        BigDecimal proposedSalary
) {
}
