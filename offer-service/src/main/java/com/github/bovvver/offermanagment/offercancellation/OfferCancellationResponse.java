package com.github.bovvver.offermanagment.offercancellation;

import com.github.bovvver.offermanagment.vo.OfferStatus;

import java.time.LocalDateTime;
import java.util.UUID;

record OfferCancellationResponse(
        UUID offerId,
        OfferStatus status,
        LocalDateTime cancelledAt
) {
    OfferCancellationResponse(UUID offerId, OfferStatus status) {
        this(offerId, status, LocalDateTime.now());
    }
}
