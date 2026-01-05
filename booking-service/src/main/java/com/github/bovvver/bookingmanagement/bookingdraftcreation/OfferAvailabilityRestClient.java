package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class OfferAvailabilityRestClient implements OfferAvailabilityClient {

    private static final String RESOLVE_BOOKING_DRAFT_ENDPOINT = "/internal/offer/availability";

    private final RestClient restClient;

    @Override
    public boolean isOfferAvailable(final UUID bookingId, final UUID offerId, final UUID userId) {
        OfferAvailabilityCheckResponse response =
                restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(RESOLVE_BOOKING_DRAFT_ENDPOINT)
                                .queryParam("offerId", offerId)
                                .queryParam("userId", userId)
                                .queryParam("bookingId", bookingId)
                                .build())
                        .retrieve()
                        .body(OfferAvailabilityCheckResponse.class);

        return response != null && response.isAvailable();
    }
}
