package com.github.bovvver.bookingmanagement.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Getter
    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Getter
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Getter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private JsonNode payload;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "processed", nullable = false)
    private boolean processed = false;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    public static OutboxEvent create(
            UUID aggregateId,
            String aggregateType,
            String eventType,
            JsonNode payload,
            LocalDateTime occurredAt
    ) {
        return new OutboxEvent(
                UUID.randomUUID(),
                aggregateId,
                aggregateType,
                eventType,
                payload,
                occurredAt,
                false,
                OutboxStatus.NEW,
                null,
                0,
                null
        );
    }

    /**
     * Handles the successful processing of the outbox event.
     * Marks the event as processed and updates its status to SENT.
     */
    public void handleSuccess() {
        handleStatusChange(true, OutboxStatus.SENT, null);
    }

    /**
     * Exponential backoff retry mechanism.
     * Handles the failure of processing the outbox event.
     * If the maximum retry count is reached, the event is marked as DEAD.
     * Otherwise, the retry count is incremented, and the next retry time is calculated
     * using an exponential backoff strategy. The event's status is updated to FAILED.
     *
     * @param errorMessage The error message describing the failure reason.
     */
    public void handleFailure(String errorMessage) {
        int MAX_RETRIES = 5;
        int BASE_DELAY_SECONDS = 5;
        this.processed = false;

        if (this.retryCount >= MAX_RETRIES) {
            handleStatusChange(false, OutboxStatus.DEAD, errorMessage);
            return;
        }

        Duration delay = Duration.ofSeconds(
                BASE_DELAY_SECONDS * (1L << this.retryCount)
        );

        this.retryCount += 1;
        this.nextRetryAt = LocalDateTime.now().plus(delay);
        handleStatusChange(false, OutboxStatus.FAILED, errorMessage);
    }

    private void handleStatusChange(boolean isProcessed, OutboxStatus newStatus, String errorMessage) {
        this.processed = isProcessed;
        this.status = newStatus;
        this.lastError = errorMessage;
    }
}