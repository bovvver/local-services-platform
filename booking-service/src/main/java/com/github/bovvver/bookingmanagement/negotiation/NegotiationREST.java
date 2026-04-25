package com.github.bovvver.bookingmanagement.negotiation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class NegotiationREST {

    private static final String SUBMIT_PROPOSAL_ENDPOINT = "/{bookingId}/negotiation/proposal";
    private static final String PROPOSAL_DECISION_ACCEPT = "/{bookingId}/negotiation/{positionId}/accept";
    private static final String PROPOSAL_DECISION_REJECT = "/{bookingId}/negotiation/{positionId}/reject";

    private final NegotiationProcessService negotiationProcessService;

    @PostMapping(path = SUBMIT_PROPOSAL_ENDPOINT)
    void makeProposal(
            @PathVariable UUID bookingId,
            @Valid @RequestBody NegotiationProposalRequest request
    ) {
        negotiationProcessService.makeProposal(bookingId, request);
    }

    @PostMapping(path = PROPOSAL_DECISION_ACCEPT)
    void acceptProposal(@PathVariable UUID bookingId, @PathVariable UUID positionId) {
        negotiationProcessService.acceptProposal(bookingId, positionId);
    }

    @PostMapping(path = PROPOSAL_DECISION_REJECT)
    void rejectProposal(@PathVariable UUID bookingId, @PathVariable UUID positionId) {
        negotiationProcessService.rejectProposal(bookingId, positionId);
    }
}
