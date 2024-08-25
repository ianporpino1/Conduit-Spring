package com.conduit.application.service;

import com.conduit.application.dto.RegisterUserDto;
import com.conduit.application.dto.UserDto;
import com.conduit.application.dto.UserResponseDto;
import com.conduit.domain.model.User;
import com.conduit.domain.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration configuration;

    public UserService(UserRepository userRepository, AuthenticationService authenticationService, PasswordEncoder passwordEncoder, AuthenticationConfiguration configuration) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
        this.configuration = configuration;
    }
    
    public UserResponseDto registerUser(UserDto userDto) throws Exception {
        if (userRepository.findUserByEmail(userDto.email()).isPresent()) {
            throw new Exception("User with this email already exists."); //UserAlreadyExistsException
        }
        
        User user = new User();
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));

        User savedUser = userRepository.save(user);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password());
        Authentication authenticated = configuration.getAuthenticationManager().authenticate(authentication);

        return authenticationService.authenticate(authenticated);
    }
}
