package com.github.bovvver.bookingmanagement.offercancellation;

import com.github.bovvver.contracts.BookingCancelledByAuthorIntegrationEvent;
import com.github.bovvver.contracts.BookingCancelledByExecutorIntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class OfferCancellationListener {

    private static final String OFFER_CANCELLED_BY_AUTHOR_TOPIC = "booking.cancelled.by-author";
    private static final String OFFER_CANCELLED_BY_EXECUTOR_TOPIC = "booking.cancelled.by-executor";

    private final OfferCancellationService offerCancellationService;

    @KafkaListener(
            topics = OFFER_CANCELLED_BY_AUTHOR_TOPIC,
            groupId = "booking-service",
            containerFactory = "bookingCancelledByAuthorFactory"
    )
    public void onOfferCancelledByAuthor(BookingCancelledByAuthorIntegrationEvent event) {
        offerCancellationService.cancelByAuthor(event.offerId());
    }

    @KafkaListener(
            topics = OFFER_CANCELLED_BY_EXECUTOR_TOPIC,
            groupId = "booking-service",
            containerFactory = "bookingCancelledByExecutorFactory"
    )
    public void onOfferCancelledByExecutor(BookingCancelledByExecutorIntegrationEvent event) {
        offerCancellationService.cancelByExecutor(event.offerId(), event.executorId());
    }
}

