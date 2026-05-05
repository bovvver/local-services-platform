package com.github.bovvver.offermanagment.offerexecution;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferMapper;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class OfferExecutionService {

    private final CurrentUser currentUser;
    private final OfferRepository offerRepository;

    StartExecutionResponse startExecution(final UUID offerId) {
        OfferDocument offerDocument = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));

        Offer offer = OfferMapper.toDomain(offerDocument);
        offer.startExecution(currentUser.getId());
        return new StartExecutionResponse(offer.getStatus(), LocalDateTime.now());
    }
}
