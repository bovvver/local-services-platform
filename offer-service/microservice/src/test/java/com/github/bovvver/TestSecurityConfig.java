package com.github.bovvver;

import com.github.bovvver.vo.UserId;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

@TestConfiguration
@Profile("test")
class TestSecurityConfig {

    private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174000";

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("xd");

        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Primary
    CurrentUser testCurrentUser() {
        return () -> new UserId(UUID.fromString(TEST_USER_ID));
    }
}
