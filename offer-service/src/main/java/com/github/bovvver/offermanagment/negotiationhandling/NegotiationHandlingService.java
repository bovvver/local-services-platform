package com.github.bovvver.offermanagment.negotiationhandling;

import com.github.bovvver.offermanagment.*;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class NegotiationHandlingService {

    private final OfferRepository offerRepository;
    private final OfferWriteRepository offerWriteRepository;

    @Transactional
    void handleNegotiationStarted(UUID offerId) {
        OfferDocument offerDocument = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalStateException("Offer not found.")
        );
        Offer offer = OfferMapper.toDomain(offerDocument);
        offer.changeStatus(OfferStatus.IN_NEGOTIATION);
        offerWriteRepository.save(offer);
    }
}
