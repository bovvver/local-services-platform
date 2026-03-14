package com.github.bovvver.offermanagment.negotiationhandling;

import com.github.bovvver.contracts.NegotiationStartedFailureIntegrationEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NegotiationFailureEventMapperTest {

    @Test
    void toIntegrationEvent() {
        UUID bookingId = UUID.randomUUID();

        NegotiationStartedFailure event = new NegotiationStartedFailure(
                bookingId
        );
        NegotiationStartedFailureIntegrationEvent integrationEvent = NegotiationFailureEventMapper.toIntegrationEvent(event);

        assertThat(integrationEvent).isNotNull();
        assertThat(integrationEvent.bookingId()).isEqualTo(bookingId);
    }
}
