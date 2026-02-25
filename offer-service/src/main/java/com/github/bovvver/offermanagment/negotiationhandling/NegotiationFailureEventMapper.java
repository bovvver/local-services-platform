package com.github.bovvver.offermanagment.negotiationhandling;

import com.github.bovvver.contracts.NegotiationStartedFailureIntegrationEvent;

public class NegotiationFailureEventMapper {

    public static NegotiationStartedFailureIntegrationEvent toIntegrationEvent(NegotiationStartedFailure event) {
        return new NegotiationStartedFailureIntegrationEvent(
                event.bookingId()
        );
    }
}
