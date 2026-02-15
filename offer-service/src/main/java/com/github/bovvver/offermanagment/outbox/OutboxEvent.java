package com.github.bovvver.offermanagment.outbox;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @Field("aggregate_type")
    private String aggregateType;

    private String type;

    private String payload;

    @Field("occurred_at")
    private LocalDateTime occurredAt;

    public static OutboxEvent create(
            UUID aggregateId,
            String aggregateType,
            String type,
            String payload,
            LocalDateTime occurredAt
    ) {
        return new OutboxEvent(
                UUID.randomUUID(),
                aggregateId,
                aggregateType,
                type,
                payload,
                occurredAt
        );
    }
}