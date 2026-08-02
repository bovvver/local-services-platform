package com.github.bovvver.reputationmanagement;

import com.github.bovvver.contracts.BookingCancelledByExecutorIntegrationEvent;
import com.github.bovvver.contracts.OfferCompletedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class ReputationDataUpdatedListener {

    private static final String OFFER_CANCELLED_BY_EXECUTOR_TOPIC = "booking.cancelled.by-executor";
    private static final String OFFER_COMPLETED_TOPIC = "offer.completed";

    private final ReputationUpdateService reputationUpdateService;

    @KafkaListener(
            topics = OFFER_CANCELLED_BY_EXECUTOR_TOPIC,
            groupId = "user-service",
            containerFactory = "bookingCancelledByExecutorFactory"
    )
    public void onBookingCancelledByExecutor(BookingCancelledByExecutorIntegrationEvent event) {
        reputationUpdateService.incrementCancelledBookingsByExecutor(event.executorId());
    }

    @KafkaListener(
            topics = OFFER_COMPLETED_TOPIC,
            groupId = "user-service",
            containerFactory = "offerCompletedFactory"
    )
    public void onOfferCompleted(OfferCompletedIntegrationEvent event) {
        reputationUpdateService.incrementCompletedBookingsByExecutor(event.executorId());
    }
}
