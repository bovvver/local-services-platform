package com.github.bovvver.offermanagment;

import com.github.bovvver.offermanagment.vo.OfferId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class OfferRepositoryImpl implements OfferRepository {
    private final MongoOfferRepository repository;

    @Override
    public Offer save(final Offer offer) {
        OfferDocument offerDocument = repository.save(OfferMapper.toDocument(offer));
        return OfferMapper.toDomain(offerDocument);
    }

    @Override
    public Optional<Offer> findById(final OfferId id) {

        Optional<OfferDocument> offerDocument = repository.findById(id.value());
        return offerDocument.map(OfferMapper::toDomain);
    }
}
