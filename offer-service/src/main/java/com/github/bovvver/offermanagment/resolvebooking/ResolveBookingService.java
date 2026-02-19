package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingAcceptedIntegrationEvent;
import com.github.bovvver.offermanagment.*;
import com.github.bovvver.offermanagment.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ResolveBookingService {

    private final OfferRepository offerRepository;
    private final OfferWriteRepository offerWriteRepository;

    @Transactional
    void completeBookingAssignment(final BookingAcceptedIntegrationEvent event) {
        OfferDocument offerDocument = offerRepository.findById(event.offerId())
                .orElseThrow(() -> new IllegalStateException(
                        "Offer with id %s not found during booking decision.".formatted(event.offerId())
                ));
        Offer offer = OfferMapper.toDomain(offerDocument);
        offer.accept(UserId.of(event.userId()));

        offerWriteRepository.save(offer);
    }
}
