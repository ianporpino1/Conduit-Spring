package com.conduit.application.service;

import com.conduit.application.dto.profile.ProfileResponseDTO;
import com.conduit.domain.model.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {
    private final UserService userService;

    public ProfileService(UserService userService) {
        this.userService = userService;
    }


    public ProfileResponseDTO getProfile(String searchedUsername, Jwt currentUserJwt){
        User searchedUser = userService.getUserByUsername(searchedUsername);

        boolean isFollowing = Optional.ofNullable(currentUserJwt)
                .map(jwt -> userService.isFollowing(currentUserJwt, searchedUser.getId()))
                .orElse(false);

        return new ProfileResponseDTO(searchedUser.getUsername(), searchedUser.getBio(), searchedUser.getImage(), isFollowing);
    }


    public ProfileResponseDTO followUserProfile(Jwt currentUserJwt, String usernameToBeFollowed) {
        return userService.followUser(currentUserJwt,usernameToBeFollowed);
    }

    public ProfileResponseDTO unfollowUserProfile(Jwt currentUserJwt, String usernameToBeUnfollowed) {
        return userService.unfollowUser(currentUserJwt, usernameToBeUnfollowed);
    }
}
