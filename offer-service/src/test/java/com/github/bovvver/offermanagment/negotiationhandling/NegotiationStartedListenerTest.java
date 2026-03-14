package com.github.bovvver.offermanagment.negotiationhandling;

import com.github.bovvver.contracts.NegotiationStartedIntegrationEvent;
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
class NegotiationStartedListenerTest {

    @Mock
    private NegotiationHandlingService negotiationHandlingService;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private NegotiationStartedListener negotiationStartedListener;

    @Test
    void onNegotiationStartedDelegatesToNegotiationHandlingService() {
        UUID offerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        NegotiationStartedIntegrationEvent event = new NegotiationStartedIntegrationEvent(
                "Negotiation started",
                offerId,
                bookingId,
                LocalDateTime.now()
        );

        negotiationStartedListener.onNegotiationStarted(event);

        verify(negotiationHandlingService).handleNegotiationStarted(offerId);
        verifyNoInteractions(outboxService);
    }

    @Test
    void onNegotiationStartedPassesFailureToOutboxWhenIllegalStateExceptionOccurs() {
        UUID offerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        var event = new NegotiationStartedIntegrationEvent(
                "Negotiation started",
                offerId,
                bookingId,
                LocalDateTime.now()
        );

        doThrow(new IllegalStateException("test"))
                .when(negotiationHandlingService)
                .handleNegotiationStarted(offerId);

        negotiationStartedListener.onNegotiationStarted(event);

        verify(outboxService).passToOutbox(
                argThat(list ->
                        list.size() == 1 &&
                                list.getFirst() instanceof NegotiationStartedFailure &&
                                ((NegotiationStartedFailure) list.getFirst()).bookingId().equals(bookingId)
                ),
                eq(bookingId),
                eq("Booking")
        );
    }
}
