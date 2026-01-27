package com.github.bovvver.contracts;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record BookOfferCommand(@NotNull UUID offerId,
                               @NotNull UUID userId,
                               @NotNull UUID bookingId,
                               BigDecimal salary) {
}
