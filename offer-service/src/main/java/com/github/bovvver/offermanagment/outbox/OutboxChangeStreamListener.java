package com.github.bovvver.offermanagment.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.offermanagment.events.ExecutorAssigned;
import com.github.bovvver.offermanagment.events.ExecutorAssignmentFailed;
import com.github.bovvver.offermanagment.events.IntegrationEvent;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Listens to MongoDB Change Stream on outbox_events collection.
 * When a new outbox event is inserted, publishes it to Kafka via EventBus.
 */
@Component
@RequiredArgsConstructor
@Slf4j
class OutboxChangeStreamListener {

    private final MongoTemplate mongoTemplate;
    private final EventBus eventBus;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void startListening() {
        Thread.startVirtualThread(() -> {
            log.info("Starting Change Stream listener on outbox_events collection");

            mongoTemplate.getCollection("outbox_events")
                    .watch()
                    .fullDocument(FullDocument.UPDATE_LOOKUP)
                    .forEach(this::handleChange);
        }).start();
    }

    private void handleChange(ChangeStreamDocument<Document> change) {
        try {
            if (change.getOperationType() != OperationType.INSERT) {
                return;
            }

            Document document = change.getFullDocument();
            if (document == null) {
                log.warn("Received change event with null fullDocument");
                return;
            }

            String eventType = document.getString("type");
            String payload = document.getString("payload");

            log.debug("Processing outbox event: type={}", eventType);

            Object integrationEvent = deserializeEvent(eventType, payload);
            if (integrationEvent != null) {
                eventBus.publish((IntegrationEvent) integrationEvent);
                log.info("Published event to Kafka: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing outbox change stream event", e);
        }
    }

    private Object deserializeEvent(String eventType, String payload) {
        try {
            return switch (eventType) {
                case "ExecutorAssigned" -> objectMapper.readValue(payload, ExecutorAssigned.class);
                case "ExecutorAssignmentFailed" -> objectMapper.readValue(payload, ExecutorAssignmentFailed.class);
                default -> {
                    log.warn("Unknown event type in outbox: {}", eventType);
                    yield null;
                }
            };
        } catch (Exception e) {
            log.error("Failed to deserialize event: type={}, payload={}", eventType, payload, e);
            return null;
        }
    }
}
