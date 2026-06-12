package com.github.bovvver.offermanagment.offercancellation;

import com.github.bovvver.contracts.BookingCancelledByAuthorIntegrationEvent;
import com.github.bovvver.contracts.BookingCancelledByExecutorIntegrationEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookingCancelledEventMapperTest {

    @Test
    void shouldMapAuthorCancellationToIntegrationEvent() {
        UUID offerId = UUID.randomUUID();

        OfferCancelledByAuthor domainEvent =
                new OfferCancelledByAuthor(offerId);

        BookingCancelledByAuthorIntegrationEvent result =
                BookingCancelledEventMapper.authorToIntegrationEvent(domainEvent);

        assertThat(result.offerId()).isEqualTo(offerId);
    }

    @Test
    void shouldMapExecutorCancellationToIntegrationEvent() {
        UUID offerId = UUID.randomUUID();
        UUID executorId = UUID.randomUUID();

        OfferCancelledByExecutor domainEvent =
                new OfferCancelledByExecutor(offerId, executorId);

        BookingCancelledByExecutorIntegrationEvent result =
                BookingCancelledEventMapper.executorToIntegrationEvent(domainEvent);

        assertThat(result.offerId()).isEqualTo(offerId);
        assertThat(result.executorId()).isEqualTo(executorId);
    }
}
