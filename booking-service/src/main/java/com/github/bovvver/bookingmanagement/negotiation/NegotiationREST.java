package com.github.bovvver.bookingmanagement.negotiation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class NegotiationREST {

    private static final String SUBMIT_PROPOSAL_ENDPOINT = "/{bookingId}/negotiation/proposal";

    private final NegotiationProcessService negotiationProcessService;

    @PostMapping(path = SUBMIT_PROPOSAL_ENDPOINT)
    void makeProposal(
            @PathVariable UUID bookingId,
            @Valid @RequestBody NegotiationProposalRequest request
    ) {
        negotiationProcessService.makeProposal(bookingId, request);
    }
}
