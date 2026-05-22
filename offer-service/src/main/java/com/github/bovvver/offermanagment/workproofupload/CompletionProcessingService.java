package com.github.bovvver.offermanagment.workproofupload;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferMapper;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
class CompletionProcessingService {

    private final CurrentUser currentUser;
    private final OfferRepository offerRepository;

    OfferExecutionResponse sendCompletionRequest(final CompletionRequest request) {

        OfferDocument offerDocument = offerRepository.findById(request.offerId())
                .orElseThrow(() -> new OfferNotFoundException(request.offerId()));
        Offer offer = OfferMapper.toDomain(offerDocument);

        offer.requestCompletion(request.description(), request.proofUrls(), currentUser.getId());

        Offer saved = OfferMapper.toDomain(offerRepository.save(OfferMapper.toDocument(offer)));

        return new OfferExecutionResponse(
                saved.getId().value(),
                saved.getStatus(),
                saved.getCompletionDescription().value(),
                saved.getWorkProofs().stream().toList(),
                LocalDateTime.now()
        );
    }
}
