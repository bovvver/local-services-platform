package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class OfferClientConfig {

    private static final String OFFER_SERVICE_BASE_URL = "http://offer-service";

    @Bean
    RestClient offerRestClient() {
        return RestClient.builder()
                .baseUrl(OFFER_SERVICE_BASE_URL)
                .build();
    }
}
