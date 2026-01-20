package com.github.bovvver.bookingmanagement.outbox;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends Repository<OutboxEvent, UUID>  {

    void save(OutboxEvent outboxEvent);

    List<OutboxEvent> findByStatusIn(OutboxStatus... statuses);
}
