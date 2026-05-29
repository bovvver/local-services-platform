package com.github.bovvver.offermanagment.workproofupload;

import com.github.bovvver.offermanagment.vo.OfferStatus;

import java.time.LocalDateTime;
import java.util.UUID;

record OfferCompletionResponse(
        UUID offerId,
        OfferStatus status,
        LocalDateTime updatedAt
) {
    OfferCompletionResponse(final UUID offerId, final OfferStatus status) {
        this(offerId, status, LocalDateTime.now());
    }
}
