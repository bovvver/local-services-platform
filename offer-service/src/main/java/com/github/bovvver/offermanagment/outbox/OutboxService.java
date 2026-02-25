package com.github.bovvver.offermanagment.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.offermanagment.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void passToOutbox(List<DomainEvent> events, UUID aggregateId, String aggregateType) {

        for (DomainEvent event : events) {
            try {
                String payload = objectMapper.writeValueAsString(event);
                OutboxEvent outboxEvent = OutboxEvent.create(
                        aggregateId,
                        aggregateType,
                        event.getClass().getSimpleName(),
                        payload,
                        LocalDateTime.now()
                );
                outboxRepository.save(outboxEvent);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize event: " + event.getClass().getSimpleName(), e);
            }
        }
    }
}
