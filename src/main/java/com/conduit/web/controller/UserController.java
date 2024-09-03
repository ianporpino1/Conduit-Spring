package com.conduit.web.controller;

import com.conduit.application.dto.user.UserRequestDTO;
import com.conduit.application.dto.user.UserResponseDTO;
import com.conduit.application.service.AuthenticationService;
import com.conduit.application.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(AuthenticationService authenticationService, UserService userService, AuthenticationManager authenticationManager) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }
    
    @PostMapping("users/login")
    public UserResponseDTO authenticate(@RequestBody UserRequestDTO loginRequest){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authenticationService.authenticate(authentication);
    }

    @PostMapping("users")
    public UserResponseDTO registerUser(@RequestBody UserRequestDTO registerUserDto) throws Exception {
        return userService.registerUser(registerUserDto);
    }       
    
    
    @GetMapping("user")
    public UserResponseDTO getCurrentUser(@AuthenticationPrincipal Jwt principal) {
        return userService.getCurrentUser(principal);
    }
    
    @PutMapping("user")
    public UserResponseDTO updateUser(@AuthenticationPrincipal Jwt principal,
                                                                   @RequestBody UserRequestDTO updateUserDto){
        return userService.updateUser(updateUserDto,principal);
    }
    
    

}
