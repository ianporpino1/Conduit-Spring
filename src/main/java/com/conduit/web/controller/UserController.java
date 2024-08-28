package com.conduit.web.controller;

import com.conduit.application.dto.user.RegisterUserDto;
import com.conduit.application.dto.user.UpdateUserDto;
import com.conduit.application.dto.UserResponseDto;
import com.conduit.application.service.AuthenticationService;
import com.conduit.application.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, UserResponseDto>> authenticate(Authentication authentication){
        UserResponseDto userResponseDto = authenticationService.authenticate(authentication);
        Map<String, UserResponseDto> response = new HashMap<>();
        response.put("user", userResponseDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("users")
    public ResponseEntity<Map<String, UserResponseDto>> registerUser(@RequestBody RegisterUserDto registerUserDto) throws Exception {
        System.out.println(registerUserDto);
        UserResponseDto userResponseDto = userService.registerUser(registerUserDto.user());
        Map<String, UserResponseDto> response = new HashMap<>();
        response.put("user", userResponseDto);
        return ResponseEntity.ok(response);
    }       
    
    
    @GetMapping("user")
    public ResponseEntity<Map<String, UserResponseDto>> getCurrentUser(@AuthenticationPrincipal Jwt principal) {
        UserResponseDto userResponseDto = userService.getCurrentUser(principal);
        
        Map<String, UserResponseDto> response = new HashMap<>();
        response.put("user", userResponseDto);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("user")
    public ResponseEntity<Map<String, UserResponseDto>> updateUser(@AuthenticationPrincipal Jwt principal,
                                                                  @RequestBody UpdateUserDto updateUserDto){
        
        UserResponseDto userResponseDto = userService.updateUser(updateUserDto.user(),principal);
        
        Map<String, UserResponseDto> response = new HashMap<>();
        response.put("user", userResponseDto);
        return ResponseEntity.ok(response);
    }
    
    

}
