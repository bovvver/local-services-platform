package com.github.bovvver.offermanagment.resolvebookingdraft;

import com.github.bovvver.contracts.BookOfferCommand;
import com.github.bovvver.contracts.BookingDraftRejectedEvent;
import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.event.DomainEventPublisher;
import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.BookingId;
import com.github.bovvver.offermanagment.vo.OfferId;
import com.github.bovvver.offermanagment.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
class ResolveBookingDraftListener {

    private static final String CREATE_BOOKING_TOPIC = "booking.commands";

    private final KafkaTemplate<String, BookingDraftRejectedEvent> kafka;
    private final OfferRepository offerRepository;
    private final DomainEventPublisher domainEventPublisher;

    @KafkaListener(topics = CREATE_BOOKING_TOPIC, groupId = "offer-service")
    void onBookingRequestReceived(BookOfferCommand cmd) {

        OfferId offerId = OfferId.of(cmd.offerId());
        Offer offer = offerRepository.findById(offerId).orElse(null);

        if (offer == null) {
            kafka.send("booking.events", cmd.bookingId().toString(),
                    new BookingDraftRejectedEvent("NOT_FOUND",
                            "Offer with id %s not found".formatted(offerId.value()),
                            cmd.bookingId(),
                            offerId.value(),
                            cmd.userId(),
                            Instant.now()));
            return;
        }

        DomainEvent domainEvent = offer.book(
                UserId.of(cmd.userId()),
                BookingId.of(cmd.bookingId())
        );

        domainEventPublisher.publish(domainEvent);
    }
}
