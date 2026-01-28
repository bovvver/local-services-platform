package com.github.bovvver.offermanagment.outbox;

import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends Repository<OutboxEvent, UUID>  {

    void save(OutboxEvent outboxEvent);

    List<OutboxEvent> findByStatusIn(OutboxStatus... statuses);

    void deleteByProcessedTrueAndOccurredAtBefore(LocalDateTime cutoffDate);
}
