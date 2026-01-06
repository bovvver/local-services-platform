package com.github.bovvver.offermanagment.resolvebookingdraft;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferMapper;
import com.github.bovvver.offermanagment.OfferReadRepository;
import com.github.bovvver.offermanagment.OfferWriteRepository;
import com.github.bovvver.offermanagment.vo.BookingDraftAccepted;
import com.github.bovvver.offermanagment.vo.BookingDraftRejected;
import com.github.bovvver.offermanagment.vo.BookingId;
import com.github.bovvver.offermanagment.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class OfferAvailabilityService {

    private final OfferReadRepository offerReadRepository;
    private final OfferWriteRepository offerWriteRepository;

    @Transactional
    OfferAvailabilityCheckResponse attemptOfferBooking(UUID offerId, UUID userId, UUID bookingId) {

        Offer offer = OfferMapper.toDomain(offerReadRepository.findById(offerId).orElse(null));
        if (offer == null) {
            return OfferAvailabilityCheckResponse.notFound();
        }

        offer.book(
                UserId.of(userId),
                BookingId.of(bookingId)
        );
        offerWriteRepository.save(offer);
        return handleResponseType(offer.pullDomainEvents());
    }

    private OfferAvailabilityCheckResponse handleResponseType(List<DomainEvent> events) {

        if (events.stream().anyMatch(e -> e instanceof BookingDraftRejected)) {
            return OfferAvailabilityCheckResponse.unavailable();
        }
        if (events.stream().anyMatch(e -> e instanceof BookingDraftAccepted)) {
            return OfferAvailabilityCheckResponse.available();
        }
        throw new IllegalStateException("No decision event found after processing booking draft.");
    }
}
