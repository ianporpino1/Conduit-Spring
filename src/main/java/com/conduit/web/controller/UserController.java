package com.conduit.web.controller;

import com.conduit.application.dto.user.UserRequestDTO;
import com.conduit.application.dto.user.UserResponseDTO;
import com.conduit.application.service.AuthenticationService;
import com.conduit.application.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public UserController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }
    
    @PostMapping("users/login")
    public UserResponseDTO authenticate(Authentication authentication){
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
