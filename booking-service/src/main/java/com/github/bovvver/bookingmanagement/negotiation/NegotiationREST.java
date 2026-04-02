package com.github.bovvver.bookingmanagement.negotiation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class NegotiationREST {

    private static final String SUBMIT_PROPOSAL_ENDPOINT = "/{bookingId}/negotiation/proposal";

    @PostMapping(path = SUBMIT_PROPOSAL_ENDPOINT)
    void submitProposal(
            @RequestParam UUID bookingId,
            @RequestBody NegotiationProposalRequest request
    ) {
        
    }
}
