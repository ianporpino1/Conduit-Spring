package com.conduit.application.service;

import com.conduit.application.dto.profile.ProfileResponseDTO;
import com.conduit.application.dto.user.UserRequestDTO;
import com.conduit.application.dto.user.UserResponseDTO;
import com.conduit.application.exception.UserAlreadyExistsException;
import com.conduit.application.exception.UserNotFoundException;
import com.conduit.domain.model.User;
import com.conduit.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
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

//    public List<User> getFollowing(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(UserNotFoundException::new);
//        return user.getFollowing();
//    }

 

    public boolean isFollowing(Jwt currentUserJwt, Long searchedUserId) {
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);
        return currentUser.getFollowing().stream()
                .anyMatch(user -> user.getId().equals(searchedUserId));
    }
    @Transactional
    public ProfileResponseDTO followUser(Jwt currentUserJwt, String followedUsername) {
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);
        User followedUser = userRepository.findUserByUsername(followedUsername)
                .orElseThrow(() -> new UserNotFoundException("User to follow not found"));

        currentUser.getFollowing().add(followedUser);

        
        
        Boolean isFollowing = true;
        return new ProfileResponseDTO(followedUser.getUsername(), followedUser.getBio(), followedUser.getImage(), isFollowing);
    }
    @Transactional
    public ProfileResponseDTO unfollowUser(Jwt currentUserJwt, String unfollowedUsername) {
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(UserNotFoundException::new);
        User unfollowedUser = userRepository.findUserByUsername(unfollowedUsername)
                .orElseThrow(() -> new UserNotFoundException("User to unfollow not found"));

        currentUser.getFollowing().remove(unfollowedUser);
        
        
        Boolean isFollowing = false;
        return new ProfileResponseDTO(unfollowedUser.getUsername(), unfollowedUser.getBio(), unfollowedUser.getImage(), isFollowing);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
