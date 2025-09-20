package com.github.bovvver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Profile("!test")
class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    private static final String ACTUATOR_PATH = "/actuator/health";
    static final String KEYCLOAK_AUTH_PATH = "/user-service/keycloak/auth/";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, KeycloakApiKeyFilter keycloakApiKeyFilter) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ACTUATOR_PATH, KEYCLOAK_AUTH_PATH + "**").permitAll()
                        .anyRequest().authenticated()
                ).addFilterBefore(keycloakApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                ).csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }
}
