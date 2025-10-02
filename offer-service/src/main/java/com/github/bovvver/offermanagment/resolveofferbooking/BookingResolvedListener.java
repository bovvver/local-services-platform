package com.github.bovvver.offermanagment.resolveofferbooking;

import com.github.bovvver.offermanagment.vo.BookingAccepted;
import com.github.bovvver.offermanagment.vo.BookingRejected;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BookingResolvedListener {

    private final KafkaTemplate<String, Object> kafka;

    @EventListener
    public void on(BookingAccepted event) {
        kafka.send("booking.events", event.getBookingId().value().toString(),
                EventMapper.toBookingAcceptedEvent(event)
        );
    }

    @EventListener
    public void on(BookingRejected event) {
        kafka.send("booking.events", event.getBookingId().value().toString(),
                EventMapper.toBookingRejectedEvent(event));
    }
}
