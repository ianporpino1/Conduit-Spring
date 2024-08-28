package com.conduit.application.dto.user;

public record UserResponseDTO(String email, String token, String username, String bio, String image) {
}
