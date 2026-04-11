package com.github.bovvver.bookingmanagement.negotiation;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

record NegotiationProposalRequest(
        @PositiveOrZero BigDecimal proposedSalary
) {
}
