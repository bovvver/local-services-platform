package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingAcceptedIntegrationEvent;
import com.github.bovvver.offermanagment.outbox.OutboxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingAcceptedListenerTest {

    @Mock
    private ResolveBookingService resolveBookingService;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private BookingAcceptedListener bookingAcceptedListener;

    @Test
    void onBookingAcceptedCompletesBookingAssignmentWhenEventIsValid() {
        UUID offerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        BookingAcceptedIntegrationEvent event = new BookingAcceptedIntegrationEvent(
                "Offer accepted",
                offerId,
                userId,
                UUID.randomUUID(),
                LocalDateTime.now()
        );

        bookingAcceptedListener.onBookingAccepted(event);

        verify(resolveBookingService).completeBookingAssignment(offerId, userId);
        verifyNoInteractions(outboxService);
    }

    @Test
    void shouldPassToOutboxWhenCompleteBookingAssignmentThrowsIllegalStateException() {
        UUID offerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        var event = new BookingAcceptedIntegrationEvent(
                "Offer accepted",
                offerId,
                userId,
                bookingId,
                LocalDateTime.now()
        );

        doThrow(new IllegalStateException("test"))
                .when(resolveBookingService)
                .completeBookingAssignment(offerId, userId);

        bookingAcceptedListener.onBookingAccepted(event);

        verify(outboxService).passToOutbox(
                argThat(list ->
                        list.size() == 1 &&
                                list.getFirst() instanceof BookingAcceptedFailure &&
                                ((BookingAcceptedFailure) list.getFirst()).bookingId().equals(bookingId)
                ),
                eq(bookingId),
                eq("Booking")
        );
    }
}
