package com.github.bovvver.offermanagment.review;

import java.util.UUID;

public record OfferReviewResponse(UUID offerId, int rating) {
}
