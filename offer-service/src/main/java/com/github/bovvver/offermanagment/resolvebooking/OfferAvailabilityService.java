package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferMapper;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.OfferWriteRepository;
import com.github.bovvver.offermanagment.vo.BookingId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class OfferAvailabilityService {

    private final OfferRepository offerRepository;
    private final OfferWriteRepository offerWriteRepository;

    @Transactional
    void attemptOfferBooking(UUID offerId, UUID bookingId) {

        Offer offer = OfferMapper.toDomain(offerRepository.findById(offerId).orElse(null));
        if (offer == null) {
            // return OfferAvailabilityCheckResponse.notFound(); // not found
            return ;
        }

        offer.book(
                BookingId.of(bookingId)
        );
        offerWriteRepository.save(offer);
        return handleResponseType(offer.pullEvents());
    }
}
