package com.github.bovvver.offermanagment;

import com.github.bovvver.offermanagment.vo.OfferId;

import java.util.Optional;

public interface OfferRepository {
    Offer save(Offer offer);

    Optional<Offer> findById(OfferId id);
}
