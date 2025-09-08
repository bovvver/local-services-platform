package com.github.bovvver;

import org.springframework.data.mongodb.repository.MongoRepository;
import reactor.util.annotation.NonNull;

import java.util.UUID;

interface MongoOfferRepository extends MongoRepository<OfferDocument, UUID> {
    OfferDocument save(@NonNull OfferDocument offerDocument);
}
