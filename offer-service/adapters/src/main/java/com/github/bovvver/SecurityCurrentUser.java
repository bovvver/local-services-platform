package com.github.bovvver;

import com.github.bovvver.vo.UserId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class SecurityCurrentUser implements CurrentUser {

    @Override
    public UserId getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof final JwtAuthenticationToken tokenAuth)) {
            throw new SecurityException("No valid JWT found.");
        }

        Jwt jwt = tokenAuth.getToken();
        String userId = jwt.getClaimAsString("sub");

        if (userId == null) {
            throw new SecurityException("No 'sub' claim found in JWT");
        }

        return new UserId(UUID.fromString(userId));
    }
}
