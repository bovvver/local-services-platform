package com.github.bovvver.offermanagment.resolvebookingdraft;

import com.github.bovvver.offermanagment.vo.BookingDraftAccepted;
import com.github.bovvver.offermanagment.vo.BookingDraftRejected;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BookingDraftResolvedListener {

    private static final String BOOKING_OFFER_AVAILABILITY_ACCEPTED = "booking.offer.availability.accepted";
    private static final String BOOKING_OFFER_AVAILABILITY_REJECTED = "booking.offer.availability.rejected";

    private final KafkaTemplate<String, Object> kafka;

    @EventListener
    public void on(BookingDraftAccepted event) {
        kafka.send(BOOKING_OFFER_AVAILABILITY_ACCEPTED, event.getBookingId().value().toString(),
                EventMapper.toBookingAcceptedEvent(event)
        );
    }

    @EventListener
    public void on(BookingDraftRejected event) {
        kafka.send(BOOKING_OFFER_AVAILABILITY_REJECTED, event.getBookingId().value().toString(),
                EventMapper.toBookingRejectedEvent(event));
    }
}
