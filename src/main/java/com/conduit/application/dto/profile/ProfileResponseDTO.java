package com.conduit.application.dto.profile;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@JsonTypeName("profile")
public record ProfileResponseDTO(String username, String bio, String image, Boolean following) {}
