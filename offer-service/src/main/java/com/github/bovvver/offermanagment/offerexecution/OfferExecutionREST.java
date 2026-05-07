package com.github.bovvver.offermanagment.offerexecution;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class OfferExecutionREST {

    static final String START_EXECUTION_URL = "/offers/{offerId}/start";

    private final OfferExecutionService offerExecutionService;

    @PostMapping(path = START_EXECUTION_URL)
    ResponseEntity<StartExecutionResponse> startExecution(@PathVariable UUID offerId) {
        return ResponseEntity.ok(offerExecutionService.startExecution(offerId));
    }
}
