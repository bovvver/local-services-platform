package com.github.bovvver.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
class OfferClientConfig {

    private static final String OFFER_SERVICE_BASE_URL = "http://offer-service";

    @Bean
    @LoadBalanced
    RestClient.Builder offerRestClientBuilder() {
        return RestClient.builder()
                .baseUrl(OFFER_SERVICE_BASE_URL)
                .requestInterceptor((request, body, execution) -> {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth instanceof JwtAuthenticationToken jwtAuth) {
                        request.getHeaders().setBearerAuth(jwtAuth.getToken().getTokenValue());
                    }
                    return execution.execute(request, body);
                });
    }

    @Bean
    RestClient offerRestClient(@LoadBalanced RestClient.Builder builder) {
        return builder.build();
    }
}
