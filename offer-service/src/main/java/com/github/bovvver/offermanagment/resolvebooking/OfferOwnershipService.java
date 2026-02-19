package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferMapper;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class OfferOwnershipService {

    private final OfferRepository offerRepository;

    @Transactional
    OfferOwnershipCheckResponse checkOfferOwnership(UUID userId, UUID offerId) {

        Offer offer = OfferMapper.toDomain(offerRepository.findById(offerId).orElse(null));

        if (offer == null) {
             return OfferOwnershipCheckResponse.notFound();
        }
        if(offer.isOwnedBy(UserId.of(userId))) {
            return OfferOwnershipCheckResponse.ownershipConfirmed();
        }
        return OfferOwnershipCheckResponse.ownershipDenied();
    }
}
