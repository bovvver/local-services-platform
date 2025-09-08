package com.github.bovvver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class OfferRepositoryImpl implements OfferRepository {
    private final MongoOfferRepository repository;

    @Override
    public Offer save(final Offer offer) {
        OfferDocument offerDocument = repository.save(OfferMapper.toDocument(offer));
        return OfferMapper.toDomain(offerDocument);
    }
}
