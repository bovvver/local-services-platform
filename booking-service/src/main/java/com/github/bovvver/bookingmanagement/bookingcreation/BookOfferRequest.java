package com.github.bovvver.bookingmanagement.bookingcreation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record BookOfferRequest(@NotNull UUID offerId,
                               @PositiveOrZero Double salary) {
}
