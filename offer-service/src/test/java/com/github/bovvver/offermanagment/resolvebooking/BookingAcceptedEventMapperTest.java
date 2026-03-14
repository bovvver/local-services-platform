package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingAcceptedFailureIntegrationEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookingAcceptedEventMapperTest {

    @Test
    void toIntegrationEventMapsBookingIdCorrectly() {
        UUID bookingId = UUID.randomUUID();
        BookingAcceptedFailure domainEvent = new BookingAcceptedFailure(bookingId);

        BookingAcceptedFailureIntegrationEvent integrationEvent = BookingAcceptedEventMapper.toIntegrationEvent(domainEvent);

        assertThat(integrationEvent.bookingId()).isEqualTo(bookingId);
    }

    @Test
    void toIntegrationEventHandlesNullBookingId() {
        BookingAcceptedFailure domainEvent = new BookingAcceptedFailure(null);

        BookingAcceptedFailureIntegrationEvent integrationEvent = BookingAcceptedEventMapper.toIntegrationEvent(domainEvent);

        assertThat(integrationEvent.bookingId()).isNull();
    }
}

