package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingDecisionStatus;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;

record BookingDecisionRequest(
        @NonNull BookingDecisionStatus status,
        @PositiveOrZero Double salary
) {
}
