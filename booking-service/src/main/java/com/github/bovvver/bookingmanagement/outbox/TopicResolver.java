package com.github.bovvver.bookingmanagement.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class TopicResolver {

    private final MessagingProperties messagingProperties;

    public String resolve(OutboxEvent outboxEvent) {
        return Optional.ofNullable(messagingProperties.getTopics().get(outboxEvent.getEventType()))
                .orElseThrow(() -> new IllegalStateException(
                        "No topic configured for event: " + outboxEvent.getEventType()
                ));
    }
}
