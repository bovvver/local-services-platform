package com.github.bovvver.offermanagment.events;

import com.github.bovvver.contracts.OfferCompletedIntegrationEvent;

public class OfferCompletedEventMapper {

    public static OfferCompletedIntegrationEvent toIntegrationEvent(final OfferCompleted offerCompleted) {
        return new OfferCompletedIntegrationEvent(
                offerCompleted.offerId(),
                offerCompleted.executorId()
        );
    }
}
