package com.conduit.application.service;

import com.conduit.application.dto.profile.ProfileResponseDTO;
import com.conduit.application.dto.user.UserRequestDTO;
import com.conduit.application.dto.user.UserResponseDTO;
import com.conduit.application.exception.UserAlreadyExistsException;
import com.conduit.application.exception.UserNotFoundException;
import com.conduit.domain.model.User;
import com.conduit.domain.model.UserFollowing;
import com.conduit.domain.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public UserResponseDTO registerUser(UserRequestDTO userDto) throws Exception {
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

    public UserResponseDTO getCurrentUser(Jwt principal){
        Long userId = authenticationService.extractUserId(principal);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return new UserResponseDTO(user.getEmail(), principal.getTokenValue(), user.getUsername(), user.getBio(), user.getImage());
    }


    public UserResponseDTO updateUser(UserRequestDTO userDto, Jwt principal){
        Long userId = authenticationService.extractUserId(principal);
        User oldUser  = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Optional.ofNullable(userDto.email())
                .filter(email -> !email.equals(oldUser.getEmail()))
                .ifPresent(email -> {
                    userRepository.findUserByEmail(email)
                            .filter(user -> !user.getId().equals(oldUser.getId()))
                            .ifPresent(user -> {
                                throw new UserAlreadyExistsException("Email is already in use by another user");
                            });
                    oldUser.setEmail(email);
                });

        Optional.ofNullable(userDto.username())
                .filter(username -> !username.equals(oldUser.getUsername()))
                .ifPresent(username -> {
                    userRepository.findUserByUsername(username)
                            .ifPresent(user -> {
                                throw new UserAlreadyExistsException("Username is already in use by another user");
                            });
                    oldUser.setUsername(username);
                });

        Optional.ofNullable(userDto.bio()).ifPresent(oldUser::setBio);
        Optional.ofNullable(userDto.image()).ifPresent(oldUser::setImage);
        Optional.ofNullable(userDto.password())
                .map(passwordEncoder::encode)
                .ifPresent(oldUser::setPassword);

        User updatedUser = userRepository.save(oldUser);

        return new UserResponseDTO(
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
    
//    public boolean isFollowing(Jwt currentUserJwt, Long searchedUserId) {
//        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
//        
//        
//    }
    

    public ProfileResponseDTO followUser(Jwt currentUserJwt, String usernameToFollow) {
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);

        User userToFollow = userRepository.findUserByUsername(usernameToFollow)
                .orElseThrow(UserNotFoundException::new);
        
        UserFollowing newFollowing = UserFollowing.of(userToFollow.getId());
        currentUser.getFollowings().add(newFollowing);

        userRepository.save(currentUser);
        
        return new ProfileResponseDTO(userToFollow.getUsername(),userToFollow.getBio(),userToFollow.getImage(),true);
    }


    public ProfileResponseDTO unfollowUser(Jwt currentUserJwt, String usernameToBeUnfollowed) {
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);

        User userToUnfollow = userRepository.findUserByUsername(usernameToBeUnfollowed)
                .orElseThrow(UserNotFoundException::new);

        currentUser.getFollowings().removeIf(following -> following.getFolloweeId().equals(userToUnfollow.getId()));
        
        System.out.println(currentUser.getFollowings().isEmpty());

        userRepository.save(currentUser);

        return new ProfileResponseDTO(userToUnfollow.getUsername(),userToUnfollow.getBio(),userToUnfollow.getImage(),false);
    }
}