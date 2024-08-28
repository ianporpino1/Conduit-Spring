package com.conduit.application.dto.comment;

import com.conduit.application.dto.article.AuthorDto;

import java.time.Instant;

public record CommentDTO(long id, Instant createdAt, Instant updatedAt, String body, AuthorDto author) {
}
