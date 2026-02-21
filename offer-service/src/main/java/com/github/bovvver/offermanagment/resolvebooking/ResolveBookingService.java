package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.offermanagment.*;
import com.github.bovvver.offermanagment.outbox.OutboxService;
import com.github.bovvver.offermanagment.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ResolveBookingService {

    private final OfferRepository offerRepository;
    private final OfferWriteRepository offerWriteRepository;
    private final OutboxService outboxService;

    @Transactional
    void completeBookingAssignment(UUID offerId, UUID userId) {
        OfferDocument offerDocument = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalStateException(
                        "Offer not found during booking decision."
                ));
        Offer offer = OfferMapper.toDomain(offerDocument);
        offer.accept(UserId.of(userId));
        offerWriteRepository.save(offer);

        outboxService.passToOutbox(offer.pullEvents(), offer.getId().value(), "Offer");
    }
}
