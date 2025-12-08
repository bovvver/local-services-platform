package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingDecisionStatus;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;

import java.math.BigDecimal;

record BookingDecisionRequest(
        @NonNull BookingDecisionStatus status,
        @Positive BigDecimal salary
) {
}
