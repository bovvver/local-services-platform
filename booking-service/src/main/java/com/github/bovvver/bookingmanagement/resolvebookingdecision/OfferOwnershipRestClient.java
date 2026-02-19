package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class OfferOwnershipRestClient implements OfferOwnershipClient {

    private static final String OFFER_OWNERSHIP_ENDPOINT = "/internal/check-ownership";

    private final RestClient restClient;

    @Override
    public boolean isUserAnOwner(final UUID userId, final UUID offerId) {
        OfferOwnershipCheckResponse response =
                restClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(OFFER_OWNERSHIP_ENDPOINT)
                                .queryParam("userId", userId)
                                .queryParam("offerId", offerId)
                                .build())
                        .retrieve()
                        .body(OfferOwnershipCheckResponse.class);

        return response != null && response.isOwner();
    }
}
