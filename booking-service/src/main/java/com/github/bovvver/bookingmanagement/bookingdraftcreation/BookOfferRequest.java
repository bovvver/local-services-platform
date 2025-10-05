package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record BookOfferRequest(@NotNull UUID offerId,
                               @PositiveOrZero Double salary) {
}
