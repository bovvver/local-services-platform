package com.github.bovvver.offermanagment.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.contracts.IntegrationEvent;
import com.github.bovvver.offermanagment.events.ExecutorAssigned;
import com.github.bovvver.offermanagment.events.ExecutorAssignmentFailed;
import com.github.bovvver.offermanagment.events.ExecutorAssignmentMapper;
import com.github.bovvver.offermanagment.negotiationhandling.NegotiationFailureEventMapper;
import com.github.bovvver.offermanagment.negotiationhandling.NegotiationStartedFailure;
import com.github.bovvver.offermanagment.resolvebooking.BookingAcceptedEventMapper;
import com.github.bovvver.offermanagment.resolvebooking.BookingAcceptedFailure;
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
        });
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

            IntegrationEvent integrationEvent = deserializeEvent(eventType, payload);
            if (integrationEvent != null) {
                eventBus.publish(integrationEvent);
                log.info("Published event to Kafka: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing outbox change stream event", e);
        }
    }

    private IntegrationEvent deserializeEvent(String eventType, String payload) {
        try {
            return switch (eventType) {
                case "ExecutorAssigned" -> ExecutorAssignmentMapper.successToIntegrationEvent(objectMapper.readValue(payload, ExecutorAssigned.class));
                case "ExecutorAssignmentFailed" -> ExecutorAssignmentMapper.failureToIntegrationEvent(objectMapper.readValue(payload, ExecutorAssignmentFailed.class));
                case "NegotiationStartedFailure" -> NegotiationFailureEventMapper.toIntegrationEvent(objectMapper.readValue(payload, NegotiationStartedFailure.class));
                case "BookingAcceptedFailure" -> BookingAcceptedEventMapper.toIntegrationEvent(objectMapper.readValue(payload, BookingAcceptedFailure.class));
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
