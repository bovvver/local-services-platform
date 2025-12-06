package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingDecisionStatus;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;

import java.math.BigDecimal;

record BookingDecisionRequest(
        @NonNull BookingDecisionStatus status,
        @PositiveOrZero BigDecimal salary
) {
}
