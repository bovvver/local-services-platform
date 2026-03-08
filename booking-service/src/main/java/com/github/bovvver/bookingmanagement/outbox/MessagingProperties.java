package com.github.bovvver.bookingmanagement.outbox;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "messaging")
@Getter
class MessagingProperties {

    private final Map<String, String> topics = new HashMap<>();
}
