package com.github.bovvver.offermanagment.outbox;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

interface OutboxRepository extends MongoRepository<OutboxEvent, UUID> {
}
