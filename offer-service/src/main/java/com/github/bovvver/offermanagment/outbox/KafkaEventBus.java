package com.github.bovvver.offermanagment.outbox;

import com.github.bovvver.offermanagment.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class KafkaEventBus implements EventBus {

    private final KafkaTemplate<String, DomainEvent> kafka;
    private final TopicResolver topicResolver;

    /**
     * Publishes the given integration event to the appropriate Kafka topic.
     *
     * @param domainEvent The event to be published. It contains the event type,
     *                    aggregate ID, and payload to be sent to the Kafka topic.
     */
    @Override
    public void publish(DomainEvent domainEvent) {
        String topic = topicResolver.resolve(domainEvent);
        kafka.send(topic, domainEvent);
    }
}
