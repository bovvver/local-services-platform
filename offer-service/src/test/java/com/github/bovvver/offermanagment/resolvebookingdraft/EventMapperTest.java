package com.github.bovvver.offermanagment.resolvebookingdraft;

import com.github.bovvver.contracts.BookingDraftAcceptedEvent;
import com.github.bovvver.contracts.BookingDraftRejectedEvent;
import com.github.bovvver.offermanagment.vo.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventMapperTest {

    @Test
    void shouldMapToBookingAcceptedEventCorrectly() {
        BookingDraftAccepted domainEvent = new BookingDraftAccepted(
                OfferId.of(UUID.randomUUID()),
                UserId.of(UUID.randomUUID()),
                BookingId.of(UUID.randomUUID())
        );

        BookingDraftAcceptedEvent result = EventMapper.toBookingAcceptedEvent(domainEvent);

        assertThat(result.status()).isEqualTo(domainEvent.getStatus().name());
        assertThat(result.message()).isEqualTo(domainEvent.getMessage());
        assertThat(result.offerId()).isEqualTo(domainEvent.getOfferId().value());
        assertThat(result.userId()).isEqualTo(domainEvent.getUserId().value());
        assertThat(result.bookingId()).isEqualTo(domainEvent.getBookingId().value());
        assertThat(result.timestamp()).isEqualTo(domainEvent.getTimestamp());
    }

    @Test
    void shouldMapToBookingRejectedEventCorrectly() {
        BookingDraftRejected domainEvent = new BookingDraftRejected(
                OfferId.of(UUID.randomUUID()),
                UserId.of(UUID.randomUUID()),
                BookingId.of(UUID.randomUUID())
        );

        BookingDraftRejectedEvent result = EventMapper.toBookingRejectedEvent(domainEvent);

        assertThat(result.error()).isEqualTo(domainEvent.getStatus().name());
        assertThat(result.reason()).isEqualTo(domainEvent.getMessage());
        assertThat(result.offerId()).isEqualTo(domainEvent.getOfferId().value());
        assertThat(result.userId()).isEqualTo(domainEvent.getUserId().value());
        assertThat(result.bookingId()).isEqualTo(domainEvent.getBookingId().value());
        assertThat(result.timestamp()).isEqualTo(domainEvent.getTimestamp());
    }
}
