package com.github.bovvver.bookingmanagement.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class KafkaEventBus implements EventBus {

    private final KafkaTemplate<String, JsonNode> kafka;
    private final TopicResolver topicResolver;

    @Override
    public void publish(OutboxEvent outboxEvent) {
        String topic = topicResolver.resolve(outboxEvent);
        kafka.send(topic, outboxEvent.getAggregateId().toString(), outboxEvent.getPayload());
    }
}
