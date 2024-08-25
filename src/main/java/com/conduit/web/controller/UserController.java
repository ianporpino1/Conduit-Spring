package com.conduit.web.controller;

import com.conduit.application.dto.RegisterUserDto;
import com.conduit.application.dto.UserResponseDto;
import com.conduit.application.service.AuthenticationService;
import com.conduit.application.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public UserController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }
    
    @PostMapping("users/login")
    public Map<String, UserResponseDto> authenticate(Authentication authentication){
        UserResponseDto userResponseDto = authenticationService.authenticate(authentication);
        Map<String, UserResponseDto> response = new HashMap<>();
        response.put("user", userResponseDto);
        return response;
    }

    @PostMapping("users")
    public Map<String, UserResponseDto> registerUser(@RequestBody RegisterUserDto registerUserDto) throws Exception {
        System.out.println(registerUserDto);
        UserResponseDto userResponseDto = userService.registerUser(registerUserDto.user());
        Map<String, UserResponseDto> response = new HashMap<>();
        response.put("user", userResponseDto);
        return response;
    }       
    
    
    @GetMapping("teste")
    public String teste() {
        return "teste";
    }

}
