package com.github.bovvver.offermanagment.offerexecution;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.*;
import com.github.bovvver.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class OfferExecutionService {

    private final CurrentUser currentUser;
    private final OfferRepository offerRepository;
    private final OfferWriteRepository offerWriteRepository;

    @Transactional
    StartExecutionResponse startExecution(final UUID offerId) {
        OfferDocument offerDocument = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));

        Offer offer = OfferMapper.toDomain(offerDocument);
        offer.startExecution(currentUser.getId());
        offerWriteRepository.save(offer);

        return new StartExecutionResponse(offer.getStatus(), LocalDateTime.now());
    }
}
