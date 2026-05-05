package com.github.bovvver.offermanagment.offerexecution;

import com.github.bovvver.offermanagment.vo.OfferStatus;

import java.time.LocalDateTime;

record StartExecutionResponse(
    OfferStatus offerStatus,
    LocalDateTime startedAt
) {
}
