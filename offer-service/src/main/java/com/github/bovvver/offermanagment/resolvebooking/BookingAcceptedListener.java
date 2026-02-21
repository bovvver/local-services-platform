package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingAcceptedIntegrationEvent;
import com.github.bovvver.offermanagment.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class BookingAcceptedListener {

    private static final String OFFER_BOOKING_DECISION_TOPIC = "booking.accepted";

    private final ResolveBookingService resolveBookingService;
    private final OutboxService outboxService;

    @KafkaListener(topics = OFFER_BOOKING_DECISION_TOPIC, groupId = "offer-service")
    public void onBookingAccepted(BookingAcceptedIntegrationEvent event) {

        try {
            resolveBookingService.completeBookingAssignment(event.offerId(), event.userId());
        } catch (IllegalStateException e) {
            outboxService.passToOutbox(
                    List.of(new BookingAcceptedFailure(event.bookingId())),
                    event.bookingId(),
                    "Booking"
            );
        }
    }
}
