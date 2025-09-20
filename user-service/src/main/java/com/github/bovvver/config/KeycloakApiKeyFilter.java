package com.github.bovvver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import reactor.util.annotation.NonNull;

import java.io.IOException;

@Component
class KeycloakApiKeyFilter extends OncePerRequestFilter {

    private final Environment environment;

    KeycloakApiKeyFilter(final Environment environment) {
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith(SecurityConfig.KEYCLOAK_AUTH_PATH)) {
            String expectedApiKey = environment.getProperty("app.keycloak.api-key");
            String apiKey = request.getHeader("X-Keycloak-API-Key");

            if (expectedApiKey == null || !expectedApiKey.equals(apiKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
