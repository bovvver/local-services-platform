package com.github.bovvver.offermanagment;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface OfferRepository extends MongoRepository<OfferDocument, UUID> {
}
