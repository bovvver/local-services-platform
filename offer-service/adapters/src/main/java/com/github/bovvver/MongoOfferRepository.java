package com.github.bovvver;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

interface MongoOfferRepository extends MongoRepository<OfferDocument, UUID> {
}
