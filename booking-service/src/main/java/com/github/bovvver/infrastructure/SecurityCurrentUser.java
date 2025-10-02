package com.github.bovvver.infrastructure;

import com.github.bovvver.bookingmanagement.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityCurrentUser implements CurrentUser {

    final static String SUB_CLAIM = "sub";
    final static String NO_VALID_JWT_MESSAGE = "No valid JWT found.";
    final static String NO_SUB_CLAIM_MESSAGE = "No 'sub' claim found in JWT";

    @Override
    public UserId getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof final JwtAuthenticationToken tokenAuth)) {
            throw new SecurityException(NO_VALID_JWT_MESSAGE);
        }

        Jwt jwt = tokenAuth.getToken();
        String userId = jwt.getClaimAsString(SUB_CLAIM);

        if (userId == null) {
            throw new SecurityException(NO_SUB_CLAIM_MESSAGE);
        }

        return new UserId(UUID.fromString(userId));
    }
}
