package com.github.bovvver.offermanagment.resolvebookingdraft;

import com.github.bovvver.contracts.BookOfferCommand;
import com.github.bovvver.contracts.BookingDraftRejectedEvent;
import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.event.DomainEventPublisher;
import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferMapper;
import com.github.bovvver.offermanagment.OfferReadRepository;
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

    private static final String BOOKING_OFFER_AVAILABILITY_REQUEST = "booking.offer.availability.request";
    private static final String BOOKING_OFFER_AVAILABILITY_REJECTED = "booking.offer.availability.rejected";

    private final KafkaTemplate<String, BookingDraftRejectedEvent> kafka;
    private final OfferReadRepository offerReadRepository;
    private final DomainEventPublisher domainEventPublisher;

    @KafkaListener(topics = BOOKING_OFFER_AVAILABILITY_REQUEST, groupId = "offer-service")
    void onBookingRequestReceived(BookOfferCommand cmd) {

        OfferId offerId = OfferId.of(cmd.offerId());
        Offer offer = OfferMapper.toDomain(offerReadRepository.findById(offerId.value()).orElse(null));

        if (offer == null) {
            kafka.send(BOOKING_OFFER_AVAILABILITY_REJECTED, cmd.bookingId().toString(),
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
