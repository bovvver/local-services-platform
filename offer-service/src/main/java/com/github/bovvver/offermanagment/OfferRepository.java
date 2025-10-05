package com.github.bovvver.offermanagment;

import com.github.bovvver.offermanagment.vo.OfferId;
import com.github.bovvver.offermanagment.vo.UserId;

import java.util.Optional;

public interface OfferRepository {
    Offer save(Offer offer);

    Optional<Offer> findById(OfferId id);

    boolean existsByIdAndOwnerId(OfferId offerId, UserId userId);
}
