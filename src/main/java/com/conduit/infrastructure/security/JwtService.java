package com.conduit.infrastructure.security;

import com.conduit.domain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    private final JwtEncoder encoder;

    private final JwtDecoder decoder;

    public JwtService(JwtEncoder encoder, JwtDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public String generateToken(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication cannot be null");
        }
        Instant now = Instant.now();
        long expiry = 3600L;
        
        UserAuthenticated userAuthenticated = (UserAuthenticated) authentication.getPrincipal();
        var userId = userAuthenticated.getUser().getId();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("spring-jwt-issuer")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(String.valueOf(userId))
                .build();
        
        JwtEncoderParameters parameters = JwtEncoderParameters
                .from(claims);

        return encoder.encode(parameters).getTokenValue();
    }
    
    public Long extractUserId(Jwt principal){
        return Long.valueOf(principal.getClaim("sub"));
    }
}
