package com.github.bovvver.offermanagment.workproofupload;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferMapper;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class CompletionProcessingService {

    private final CurrentUser currentUser;
    private final OfferRepository offerRepository;

    OfferExecutionResponse sendCompletionRequest(final CompletionRequest request) {

        Offer offer = getOfferById(request.offerId());
        offer.requestCompletion(request.description(), request.proofUrls(), currentUser.getId());
        Offer saved = OfferMapper.toDomain(offerRepository.save(OfferMapper.toDocument(offer)));

        return new OfferExecutionResponse(
                saved.getId().value(),
                saved.getStatus(),
                saved.getExecutionDetails().getCompletionDescription().value(),
                saved.getExecutionDetails().getWorkProofs().stream().toList(),
                saved.getExecutionDetails().getCompletionRequestedAt()
        );
    }

    OfferCompletionResponse acceptCompletion(final UUID offerId) {

        Offer offer = getOfferById(offerId);
        offer.acceptCompletion(currentUser.getId());
        Offer saved = OfferMapper.toDomain(offerRepository.save(OfferMapper.toDocument(offer)));

        return new OfferCompletionResponse(
                saved.getId().value(),
                saved.getStatus()
        );
    }

    OfferCompletionResponse rejectCompletion(final UUID offerId, final String reason) {

        Offer offer = getOfferById(offerId);
        offer.rejectCompletion(currentUser.getId(), reason);
        Offer saved = OfferMapper.toDomain(offerRepository.save(OfferMapper.toDocument(offer)));

        return new OfferCompletionResponse(
                saved.getId().value(),
                saved.getStatus()
        );
    }

    private Offer getOfferById(final UUID offerId) {
        OfferDocument offerDocument = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));
        return OfferMapper.toDomain(offerDocument);
    }
}
