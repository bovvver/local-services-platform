package com.github.bovvver;

import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.UUID;

import static com.github.bovvver.SecurityCurrentUser.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SecurityCurrentUserTest {

    @Test
    void returnsUserIdWhenJwtIsValid() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString(SUB_CLAIM)).thenReturn(UUID.randomUUID().toString());

        JwtAuthenticationToken tokenAuth = mock(JwtAuthenticationToken.class);
        when(tokenAuth.getToken()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(tokenAuth);

        try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            SecurityCurrentUser securityCurrentUser = new SecurityCurrentUser();
            UserId userId = securityCurrentUser.getId();

            assertThat(userId).isNotNull();
            assertThat(userId.value()).isEqualTo(UUID.fromString(jwt.getClaimAsString(SUB_CLAIM)));
        }
    }

    @Test
    void throwsExceptionWhenAuthenticationIsNotJwtAuthenticationToken() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            SecurityCurrentUser securityCurrentUser = new SecurityCurrentUser();

            assertThatThrownBy(securityCurrentUser::getId)
                    .isInstanceOf(SecurityException.class)
                    .hasMessage(NO_VALID_JWT_MESSAGE);
        }
    }

    @Test
    void throwsExceptionWhenSubClaimIsMissing() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString(SUB_CLAIM)).thenReturn(null);

        JwtAuthenticationToken tokenAuth = mock(JwtAuthenticationToken.class);
        when(tokenAuth.getToken()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(tokenAuth);

        try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            SecurityCurrentUser securityCurrentUser = new SecurityCurrentUser();

            assertThatThrownBy(securityCurrentUser::getId)
                    .isInstanceOf(SecurityException.class)
                    .hasMessage(NO_SUB_CLAIM_MESSAGE);
        }
    }

    @Test
    void throwsExceptionWhenAuthenticationIsNull() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            SecurityCurrentUser securityCurrentUser = new SecurityCurrentUser();

            assertThatThrownBy(securityCurrentUser::getId)
                    .isInstanceOf(SecurityException.class)
                    .hasMessage(NO_VALID_JWT_MESSAGE);
        }
    }
}