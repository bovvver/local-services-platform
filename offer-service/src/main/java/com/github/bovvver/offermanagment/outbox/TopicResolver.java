package com.github.bovvver.offermanagment.outbox;

import com.github.bovvver.offermanagment.events.IntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class TopicResolver {

    private final MessagingProperties messagingProperties;

    /**
     * Resolves the topic name for the provided integration event.
     *
     * @param integrationEvent The integration event for which the topic needs to be resolved.
     * @return The name of the topic associated with the event type.
     * @throws IllegalStateException if no topic is configured for the given event type.
     */
    public String resolve(IntegrationEvent integrationEvent) {

        String eventName = integrationEvent.getClass().getSimpleName();
        return Optional.ofNullable(messagingProperties.getTopics().get(eventName))
                .orElseThrow(() -> new IllegalStateException("No topic configured for event: " + eventName));
    }
}
