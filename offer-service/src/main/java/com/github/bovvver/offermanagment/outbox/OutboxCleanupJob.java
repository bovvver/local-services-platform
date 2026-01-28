package com.github.bovvver.offermanagment.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Transactional
@RequiredArgsConstructor
class OutboxCleanupJob {

    private final OutboxRepository outboxRepository;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanUp() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        outboxRepository.deleteByProcessedTrueAndOccurredAtBefore(cutoffDate);
    }
}
