package com.github.bovvver.offermanagment.workproofupload;

import com.github.bovvver.offermanagment.vo.OfferStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

record OfferExecutionResponse(
        UUID offerId,
        OfferStatus status,
        String completionDescription,
        List<WorkProof> proofs,
        LocalDateTime completionRequestedAt
) {
    OfferExecutionResponse(final UUID offerId, final OfferStatus status, final String completionDescription, final List<WorkProof> proofs) {
        this(offerId, status, completionDescription, proofs, LocalDateTime.now());
    }
}
