package com.conduit.application.dto.user;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@JsonTypeName("user")
public record UserRequestDTO(
        String username,
        String email,
        String password,
        String bio,
        String image
) {}
