package com.github.bovvver.offermanagment;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

interface MongoOfferRepository extends MongoRepository<OfferDocument, UUID> {

    boolean existsByIdAndAuthorId(UUID offerId, UUID authorId);
}
