package com.conduit.application.dto;

public record UserResponseDto(String email, String token, String username, String bio, String image) {
}
