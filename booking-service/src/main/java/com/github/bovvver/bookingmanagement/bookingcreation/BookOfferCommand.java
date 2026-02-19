package com.github.bovvver.bookingmanagement.bookingcreation;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

record BookOfferCommand(@NotNull UUID offerId,
                               @NotNull UUID userId,
                               @NotNull UUID bookingId,
                               BigDecimal salary) {
}
