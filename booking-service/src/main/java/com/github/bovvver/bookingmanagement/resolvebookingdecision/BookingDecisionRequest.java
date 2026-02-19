package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import jakarta.validation.constraints.Positive;
import lombok.NonNull;

import java.math.BigDecimal;

record BookingDecisionRequest(
        @NonNull BookingDecisionStatus status,
        @Positive BigDecimal salary
) {
}
