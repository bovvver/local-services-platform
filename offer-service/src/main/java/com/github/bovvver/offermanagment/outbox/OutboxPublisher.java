package com.github.bovvver.offermanagment.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Component responsible for publishing outbox events to the event bus.
 * It periodically retrieves events with status NEW or FAILED from the outbox repository,
 * attempts to publish them, and updates their status accordingly.
 */
@Component
@Transactional
@RequiredArgsConstructor
class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final EventBus eventBus;

    @Scheduled(fixedDelay = 1000)
    public void publish() {
        List<OutboxEvent> events = outboxRepository.findByStatusIn(OutboxStatus.NEW, OutboxStatus.FAILED);
        for (OutboxEvent event : events) {
            try {
                eventBus.publish(event);
                event.handleSuccess();
                outboxRepository.save(event);
            } catch (Exception e) {
                event.handleFailure(e.getMessage());
                outboxRepository.save(event);
            }
        }
    }
}
