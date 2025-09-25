package com.github.bovvver.contracts;

import java.util.UUID;

public record BookOfferCommand(UUID offerId,
                               UUID bookingId,
                               UUID userId) {
}
