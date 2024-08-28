package com.conduit.web.controller;

import com.conduit.application.dto.profile.ProfileDTO;
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
    public ResponseEntity<Map<String, ProfileDTO>> getProfile(@AuthenticationPrincipal Jwt principal,
                                                              @PathVariable String username){
        ProfileDTO profileDto = profileService.getProfile(username, principal);

        Map<String, ProfileDTO> response = new HashMap<>();
        response.put("profile", profileDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{usernameToFollow}/follow")
    public ResponseEntity<Map<String, ProfileDTO>> followUserProfile(@AuthenticationPrincipal Jwt principal,
                                                                     @PathVariable String usernameToFollow){
        ProfileDTO followedProfileDTO = profileService.followUserProfile(principal, usernameToFollow);

        Map<String, ProfileDTO> response = new HashMap<>();
        response.put("profile", followedProfileDTO);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{usernameToUnfollow}/follow")
    public ResponseEntity<Map<String, ProfileDTO>> unfollowUserProfile(@AuthenticationPrincipal Jwt principal,
                                                                       @PathVariable String usernameToUnfollow){
        ProfileDTO unfollowedProfileDTO = profileService.unfollowUserProfile(principal, usernameToUnfollow);
        Map<String, ProfileDTO> response = new HashMap<>();
        response.put("profile", unfollowedProfileDTO);
        return ResponseEntity.ok(response);
    }
}
