package com.github.bovvver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class OfferRepositoryImpl implements OfferRepository {
    private final MongoOfferRepository repository;

}
