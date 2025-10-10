package com.github.bovvver.offermanagment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class OfferWriteRepositoryImpl implements OfferWriteRepository {
    private final MongoOfferRepository repository;

    @Override
    public Offer save(final Offer offer) {
        OfferDocument offerDocument = repository.save(OfferMapper.toDocument(offer));
        return OfferMapper.toDomain(offerDocument);
    }
}
