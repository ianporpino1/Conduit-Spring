package com.conduit.application.service;

import com.conduit.application.dto.profile.ProfileDto;
import com.conduit.domain.model.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final UserService userService;

    public ProfileService(UserService userService) {
        this.userService = userService;
    }


    public ProfileDto getProfile(String searchedUsername, Jwt currentUserJwt){
        User searchedUser = userService.getUserByUsername(searchedUsername);
        
        boolean isFollowing = false;
        if(currentUserJwt != null){
            Long searchedUserId = searchedUser.getId();
            isFollowing = userService.isFollowing(currentUserJwt, searchedUserId);
        }

        return new ProfileDto(searchedUser.getUsername(), searchedUser.getBio(), searchedUser.getImage(), isFollowing);
    }


    public ProfileDto followUserProfile(Jwt currentUserJwt, String usernameToBeFollowed) {
        return userService.followUser(currentUserJwt,usernameToBeFollowed);
    }

    public ProfileDto unfollowUserProfile(Jwt currentUserJwt, String usernameToBeUnfollowed) {
        return userService.unfollowUser(currentUserJwt, usernameToBeUnfollowed);
    }
}
