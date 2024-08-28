package com.conduit.web.controller;

import com.conduit.application.dto.user.RegisterUserDTO;
import com.conduit.application.dto.user.UpdateUserDTO;
import com.conduit.application.dto.user.UserResponseDTO;
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
    public ResponseEntity<Map<String, UserResponseDTO>> authenticate(Authentication authentication){
        UserResponseDTO userResponseDto = authenticationService.authenticate(authentication);
        Map<String, UserResponseDTO> response = new HashMap<>();
        response.put("user", userResponseDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("users")
    public ResponseEntity<Map<String, UserResponseDTO>> registerUser(@RequestBody RegisterUserDTO registerUserDto) throws Exception {
        UserResponseDTO userResponseDto = userService.registerUser(registerUserDto.user());
        Map<String, UserResponseDTO> response = new HashMap<>();
        response.put("user", userResponseDto);
        return ResponseEntity.ok(response);
    }       
    
    
    @GetMapping("user")
    public ResponseEntity<Map<String, UserResponseDTO>> getCurrentUser(@AuthenticationPrincipal Jwt principal) {
        UserResponseDTO userResponseDto = userService.getCurrentUser(principal);
        
        Map<String, UserResponseDTO> response = new HashMap<>();
        response.put("user", userResponseDto);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("user")
    public ResponseEntity<Map<String, UserResponseDTO>> updateUser(@AuthenticationPrincipal Jwt principal,
                                                                   @RequestBody UpdateUserDTO updateUserDto){
        
        UserResponseDTO userResponseDto = userService.updateUser(updateUserDto.user(),principal);
        
        Map<String, UserResponseDTO> response = new HashMap<>();
        response.put("user", userResponseDto);
        return ResponseEntity.ok(response);
    }
    
    

}
