package com.github.bovvver.offermanagment.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    private UUID id;

    @Field("aggregate_id")
    private UUID aggregateId;

    private String aggregateType;
    private String eventType;

    private String payload;

    @Field("occurred_at")
    private LocalDateTime occurredAt;

    private boolean processed = false;
    private OutboxStatus status;

    @Field("last_error")
    private String lastError;

    @Field("retry_count")
    private int retryCount = 0;

    @Field("next_retry_at")
    private LocalDateTime nextRetryAt;

    @Version
    private Long version;

    public static OutboxEvent create(
            UUID aggregateId,
            String aggregateType,
            String eventType,
            String payload,
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
                null,
                0L
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

    public JsonNode getPayloadAsJson() {
        if (payload == null) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(this.payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse payload JSON", e);
        }
    }

    private void handleStatusChange(boolean isProcessed, OutboxStatus newStatus, String errorMessage) {
        this.processed = isProcessed;
        this.status = newStatus;
        this.lastError = errorMessage;
    }
}