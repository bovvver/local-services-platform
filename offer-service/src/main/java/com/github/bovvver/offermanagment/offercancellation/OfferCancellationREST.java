package com.github.bovvver.offermanagment.offercancellation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class OfferCancellationREST {

    static final String CANCEL_OFFER_URL = "/offers/{offerId}/cancel";

    private final OfferCancellationService offerCancellationService;

    @PostMapping(path = CANCEL_OFFER_URL)
    ResponseEntity<OfferCancellationResponse> cancelOffer(@PathVariable UUID offerId) {
        return ResponseEntity.ok(offerCancellationService.cancelOffer(offerId));
    }
}
