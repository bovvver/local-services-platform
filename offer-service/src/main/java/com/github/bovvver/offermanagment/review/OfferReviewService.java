package com.github.bovvver.offermanagment.review;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferMapper;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.outbox.OutboxService;
import com.github.bovvver.offermanagment.vo.Rating;
import com.github.bovvver.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class OfferReviewService {

    private final CurrentUser currentUser;
    private final OfferRepository offerRepository;
    private final OutboxService outboxService;

    @Transactional
    OfferReviewResponse submitReview(UUID offerId, int rating) {
        OfferDocument offerDocument = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));
        Offer offer = OfferMapper.toDomain(offerDocument);

        offer.submitReview(currentUser.getId(), Rating.of(rating));

        Offer saved = OfferMapper.toDomain(offerRepository.save(OfferMapper.toDocument(offer)));
        outboxService.passToOutbox(offer.pullEvents(), saved.getId().value(), "Offer");

        return new OfferReviewResponse(saved.getId().value(), saved.getRating().value());
    }
}
