package com.github.bovvver.gateway_server.config;

import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import static com.github.bovvver.gateway_server.config.SecurityConfig.KEYCLOAK_AUTH_PATH;

@Component
class KeycloakApiKeyFilter implements WebFilter {

    private final Environment environment;

    KeycloakApiKeyFilter(final Environment environment) {
        this.environment = environment;
    }

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getURI().getPath().startsWith(KEYCLOAK_AUTH_PATH)) {
            String expectedApiKey = environment.getProperty("app.keycloak.api-key");
            String apiKey = request.getHeaders().getFirst("X-Keycloak-API-Key");

            if (expectedApiKey == null || !expectedApiKey.equals(apiKey)) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                DataBuffer buffer = response.bufferFactory().wrap("Unauthorized".getBytes());
                return response.writeWith(Mono.just(buffer));
            }
        }

        return chain.filter(exchange);
    }
}
