package com.github.bovvver.bookingmanagement.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class TopicResolver {

    private final MessagingProperties messagingProperties;

    /**
     * Resolves the topic name for the provided outbox event.
     *
     * @param outboxEvent The outbox event for which the topic needs to be resolved.
     * @return The name of the topic associated with the event type.
     * @throws IllegalStateException if no topic is configured for the given event type.
     */
    public String resolve(OutboxEvent outboxEvent) {
        return Optional.ofNullable(messagingProperties.getTopics().get(outboxEvent.getEventType()))
                .orElseThrow(() -> new IllegalStateException(
                        "No topic configured for event: " + outboxEvent.getEventType()
                ));
    }
}
