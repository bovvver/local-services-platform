package com.github.bovvver.bookingmanagement.bookingcreation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record BookOfferRequest(@NotNull UUID offerId,
                               @Positive BigDecimal salary) {
}
