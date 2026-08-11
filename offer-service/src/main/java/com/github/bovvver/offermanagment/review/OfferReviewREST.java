package com.github.bovvver.offermanagment.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class OfferReviewREST {

    private final OfferReviewService offerReviewService;

    @PostMapping("/offers/{offerId}/review")
    ResponseEntity<OfferReviewResponse> submitReview(
            @PathVariable UUID offerId,
            @Valid @RequestBody SubmitReviewRequest request) {
        return ResponseEntity.ok(offerReviewService.submitReview(offerId, request.rating()));
    }
}
