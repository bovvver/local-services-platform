package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import com.github.bovvver.contracts.BookingDraftAcceptedEvent;
import com.github.bovvver.contracts.BookingDraftRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ResolveBookingDraftListener {

    private static final String BOOKING_OFFER_AVAILABILITY_ACCEPTED = "booking.offer.availability.accepted";
    private static final String BOOKING_OFFER_AVAILABILITY_REJECTED = "booking.offer.availability.rejected";

    private final BookingDraftCreationService bookingDraftCreationService;

    @KafkaListener(topics = BOOKING_OFFER_AVAILABILITY_ACCEPTED, groupId = "booking-service")
    void onBookingAcceptedEventReceived(BookingDraftAcceptedEvent event) {
        bookingDraftCreationService.createBooking(event);
    }

    @KafkaListener(topics = BOOKING_OFFER_AVAILABILITY_REJECTED, groupId = "booking-service")
    void onBookingRejectedEventReceived(BookingDraftRejectedEvent event) {
        bookingDraftCreationService.deleteDraftBooking(event.bookingId());
    }
}
