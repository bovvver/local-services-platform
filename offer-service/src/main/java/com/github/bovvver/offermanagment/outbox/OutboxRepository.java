package com.github.bovvver.offermanagment.outbox;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends MongoRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusIn(OutboxStatus... statuses);

    void deleteByProcessedTrueAndOccurredAtBefore(LocalDateTime cutoffDate);
}
