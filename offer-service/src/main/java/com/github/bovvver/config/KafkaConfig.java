package com.github.bovvver.config;

import com.github.bovvver.contracts.BookingAcceptedIntegrationEvent;
import com.github.bovvver.contracts.NegotiationStartedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
class KafkaConfig {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ───── Factories ─────

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingAcceptedIntegrationEvent> bookingAcceptedFactory() {
        return buildFactory(BookingAcceptedIntegrationEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NegotiationStartedIntegrationEvent> negotiationStartedFactory() {
        return buildFactory(NegotiationStartedIntegrationEvent.class);
    }

    // ───── Shared builders ─────

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> buildFactory(Class<T> targetType) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory(targetType));
        factory.setCommonErrorHandler(errorHandler());

        return factory;
    }

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, targetType.getName());

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(targetType, false))
        );
    }

    private DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate),
                new FixedBackOff(1000L, 3)
        );
    }
}
