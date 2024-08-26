package com.conduit.web.controller;

import com.conduit.application.dto.ProfileDto;
import com.conduit.application.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("profiles")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, ProfileDto>> getProfile(@AuthenticationPrincipal Jwt principal, 
                                                              @PathVariable String username){
        ProfileDto profileDto = profileService.getProfile(username, principal);

        Map<String, ProfileDto> response = new HashMap<>();
        response.put("profile", profileDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{usernameToFollow}/follow")
    public ResponseEntity<Map<String, ProfileDto>> followUserProfile(@AuthenticationPrincipal Jwt principal,
                                                              @PathVariable String usernameToFollow){
        ProfileDto followedProfileDto = profileService.followUserProfile(principal, usernameToFollow);

        Map<String, ProfileDto> response = new HashMap<>();
        response.put("profile", followedProfileDto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{usernameToUnfollow}/follow")
    public ResponseEntity<Map<String, ProfileDto>> unfollowUserProfile(@AuthenticationPrincipal Jwt principal,
                                                                       @PathVariable String usernameToUnfollow){
        ProfileDto unfollowedProfileDto = profileService.unfollowUserProfile(principal, usernameToUnfollow);
        Map<String, ProfileDto> response = new HashMap<>();
        response.put("profile", unfollowedProfileDto);
        return ResponseEntity.ok(response);
    }
}
