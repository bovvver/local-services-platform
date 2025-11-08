package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.contracts.OtherBookingsRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class OtherBookingsRejectedEventListener {

    private static final String OFFER_BOOKING_REJECT_OTHERS = "offer.booking.reject.others";

    private final ResolveBookingService resolveBookingService;

    @KafkaListener(topics = OFFER_BOOKING_REJECT_OTHERS, groupId = "booking-service")
    void onOtherBookingsRejectedEvent(OtherBookingsRejectedEvent event) {
        resolveBookingService.rejectOtherBookings(event);
    }
}
