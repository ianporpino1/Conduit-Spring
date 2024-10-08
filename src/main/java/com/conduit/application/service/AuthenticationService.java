package com.conduit.application.service;


import com.conduit.application.dto.user.UserResponseDTO;
import com.conduit.domain.model.User;
import com.conduit.infrastructure.security.JwtService;
import com.conduit.infrastructure.security.UserAuthenticated;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final JwtService jwtService;
    
    public AuthenticationService(JwtService jwtService) {
        this.jwtService = jwtService;
        
    }
    public UserResponseDTO authenticate(Authentication authentication) {
        UserAuthenticated UserAuthenticated  = (com.conduit.infrastructure.security.UserAuthenticated) authentication.getPrincipal();
        String token = jwtService.generateToken(authentication);
        User user = UserAuthenticated.getUser();
        return new UserResponseDTO(user.getEmail(), token, user.getUsername(), user.getBio(), user.getImage());
    }

    public Long extractUserId(Jwt principal) {
        return jwtService.extractUserId(principal);
    }
    
}
