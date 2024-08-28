package com.conduit.application.service;

import com.conduit.application.dto.profile.ProfileDto;
import com.conduit.application.dto.UserDto;
import com.conduit.application.dto.UserResponseDto;
import com.conduit.application.exception.UserAlreadyExistsException;
import com.conduit.application.exception.UserNotFoundException;
import com.conduit.domain.model.User;
import com.conduit.domain.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration configuration;

    public UserService(UserRepository userRepository, AuthenticationService authenticationService, PasswordEncoder passwordEncoder, AuthenticationConfiguration configuration) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
        this.configuration = configuration;
    }
    
    public UserResponseDto registerUser(UserDto userDto) throws Exception {
        if (userRepository.findUserByEmail(userDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists.");
        }
        if (userRepository.findUserByUsername(userDto.username()).isPresent()) {
            throw new UserAlreadyExistsException("User with this username already exists.");
        }
        
        User user = new User();
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));

        userRepository.save(user);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDto.email(), userDto.password());
        Authentication authenticated = configuration.getAuthenticationManager().authenticate(authentication);

        return authenticationService.authenticate(authenticated);
    }

    public UserResponseDto getCurrentUser(Jwt principal){
        Long userId = authenticationService.extractUserId(principal);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return new UserResponseDto(user.getEmail(), principal.getTokenValue(), user.getUsername(), user.getBio(), user.getImage());
    }
    
    
    public UserResponseDto updateUser(UserDto userDto, Jwt principal){
        Long userId = authenticationService.extractUserId(principal);
        User oldUser  = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (userDto.email() != null && !userDto.email().equals(oldUser.getEmail())) {
            User existingUser = userRepository.findUserByEmail(userDto.email())
                    .orElse(null);
            if (existingUser != null && !existingUser.getId().equals(oldUser.getId())) {
                throw new UserAlreadyExistsException("Email is already in use by another user");
            }

            oldUser.setEmail(userDto.email());
        }

        if (userDto.username() != null && !userDto.username().equals(oldUser.getUsername())) {
            userRepository.findUserByUsername(userDto.username())
                    .orElseThrow(() -> new UserAlreadyExistsException("Username is already in use by another user"));
            
            oldUser.setUsername(userDto.username());
        }
        if (userDto.bio() != null) {
            oldUser.setBio(userDto.bio());
        }
        if (userDto.image() != null) {
            oldUser.setImage(userDto.image());
        }
        if (userDto.password() != null) {
            oldUser.setPassword(passwordEncoder.encode(userDto.password()));
        }
        
        User updatedUser = userRepository.save(oldUser);

        return new UserResponseDto(
                updatedUser.getEmail(),
                principal.getTokenValue(),
                updatedUser.getUsername(),
                updatedUser.getBio(),
                updatedUser.getImage()
        );
    }
    
    public User getUserByUsername(String username){
        return userRepository.findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public List<User> getFollowing(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return user.getFollowing();
    }

    public List<User> getFollowedBy(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return user.getFollowedBy();
    }

    public boolean isFollowing(Jwt currentUserJwt, Long searchedUserId) {
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);
        return currentUser.getFollowing().stream()
                .anyMatch(user -> user.getId().equals(searchedUserId));
    }

    public ProfileDto followUser(Jwt currentUserJwt, String followedUsername) {
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);
        User followedUser = userRepository.findUserByUsername(followedUsername)
                .orElseThrow(() -> new UserNotFoundException("User to follow not found"));

        currentUser.getFollowing().add(followedUser);
        followedUser.getFollowedBy().add(currentUser);

        userRepository.save(currentUser);
        userRepository.save(followedUser);
        
        Boolean isFollowing = true;
        return new ProfileDto(followedUser.getUsername(), followedUser.getBio(), followedUser.getImage(), isFollowing);
    }

    public ProfileDto unfollowUser(Jwt currentUserJwt, String unfollowedUsername) {
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);
        User unfollowedUser = userRepository.findUserByUsername(unfollowedUsername)
                .orElseThrow(() -> new UserNotFoundException("User to unfollow not found"));

        currentUser.getFollowing().remove(unfollowedUser);
        unfollowedUser.getFollowedBy().remove(currentUser);

        userRepository.save(currentUser);
        userRepository.save(unfollowedUser);
        Boolean isFollowing = false;
        return new ProfileDto(unfollowedUser.getUsername(), unfollowedUser.getBio(), unfollowedUser.getImage(), isFollowing);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
