package com.github.bovvver.bookingmanagement.negotiation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NegotiationFacade {

    private final ResolveNegotiationDecisionService resolveNegotiationDecisionService;

    public void beginNegotiation(UUID bookingId, UUID offerAuthorId, BigDecimal salary) {
        resolveNegotiationDecisionService.beginNegotiation(bookingId, offerAuthorId, salary);
    }
}
