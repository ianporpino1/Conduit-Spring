package com.conduit.application.service;

import com.conduit.application.dto.UserResponseDto;
import com.conduit.domain.model.User;
import com.conduit.infrastructure.security.JwtService;
import com.conduit.infrastructure.security.UserAuthenticated;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final JwtService jwtService;
    

    public AuthenticationService(JwtService jwtService) {
        this.jwtService = jwtService;
        
    }
    public UserResponseDto authenticate(Authentication authentication) {
        UserAuthenticated UserAuthenticated  = (com.conduit.infrastructure.security.UserAuthenticated) authentication.getPrincipal();
        String token = jwtService.generateToken(authentication);
        User user = UserAuthenticated.getUser();
        return new UserResponseDto(user.getEmail(), token, user.getUsername(), user.getBio(), user.getImage());
    }
    
}
