package com.github.bovvver.bookingmanagement.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

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
                null
        );
    }

    public void markSent() {
        this.processed = true;
        this.status = OutboxStatus.SENT;
        this.errorMessage = null;
    }

    public void markFailed(String errorMessage) {
        this.processed = false;
        this.status = OutboxStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}