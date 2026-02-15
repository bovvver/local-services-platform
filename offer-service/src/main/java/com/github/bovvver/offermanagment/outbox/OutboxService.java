package com.github.bovvver.offermanagment.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.offermanagment.*;
import com.github.bovvver.offermanagment.events.IntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OfferWriteRepository offerRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveAndPassToOutbox(final Offer offer, String aggregateType) {
        offerRepository.save(offer);

        List<IntegrationEvent> events = offer.pullEvents();
        for (IntegrationEvent event : events) {
            try {
                String payload = objectMapper.writeValueAsString(event);
                OutboxEvent outboxEvent = OutboxEvent.create(
                        offer.getId().value(),
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
