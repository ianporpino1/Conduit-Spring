package com.conduit.web.controller;

import com.conduit.application.dto.profile.ProfileResponseDTO;
import com.conduit.application.service.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("profiles")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{username}")
    public ProfileResponseDTO getProfile(@AuthenticationPrincipal Jwt principal,
                                         @PathVariable String username){
        return profileService.getProfile(username, principal);
    }
    
    @PostMapping("/{usernameToFollow}/follow")
    public ProfileResponseDTO followUserProfile(@AuthenticationPrincipal Jwt principal,
                                                                     @PathVariable String usernameToFollow){
        return profileService.followUserProfile(principal, usernameToFollow);
    }
    
    @DeleteMapping("/{usernameToUnfollow}/follow")
    public ProfileResponseDTO unfollowUserProfile(@AuthenticationPrincipal Jwt principal,
                                                                       @PathVariable String usernameToUnfollow){
        return profileService.unfollowUserProfile(principal, usernameToUnfollow);
    }
}
