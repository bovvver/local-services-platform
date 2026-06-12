package com.github.bovvver.offermanagment.offercancellation;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.*;
import com.github.bovvver.offermanagment.outbox.OutboxService;
import com.github.bovvver.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class OfferCancellationService {

    private final CurrentUser currentUser;
    private final OfferRepository offerRepository;
    private final OfferWriteRepository offerWriteRepository;
    private final OutboxService outboxService;

    OfferCancellationResponse cancelOffer(final UUID offerId) {

        OfferDocument offerDocument = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));
        Offer offer = OfferMapper.toDomain(offerDocument);

        offer.cancel(currentUser.getId());
        offerWriteRepository.save(offer);

        outboxService.passToOutbox(offer.pullEvents(), offerId, "Offer");

        return new OfferCancellationResponse(
                offerId,
                offer.getStatus()
        );
    }
}
