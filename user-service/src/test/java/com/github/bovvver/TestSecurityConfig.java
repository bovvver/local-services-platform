package com.github.bovvver;

import com.github.bovvver.shared.CurrentUser;
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
public class TestSecurityConfig {

    /**
     * A fixed UUID used as the "logged-in" user in all integration tests.
     * Exposed as a public constant so test classes can pre-create the
     * corresponding user / profile rows without duplicating the value.
     */
    public static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Primary
    CurrentUser testCurrentUser() {
        return () -> UserId.of(TEST_USER_ID);
    }
}
