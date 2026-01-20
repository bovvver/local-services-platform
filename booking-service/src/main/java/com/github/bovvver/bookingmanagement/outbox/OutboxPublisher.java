package com.github.bovvver.bookingmanagement.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
                event.markSent();
            } catch (Exception e) {
                event.markFailed(e.getMessage());
            }
        }
    }
}
