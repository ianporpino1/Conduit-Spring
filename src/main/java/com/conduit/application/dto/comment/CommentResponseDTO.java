package com.conduit.application.dto.comment;

import com.conduit.application.dto.article.AuthorDTO;
import java.time.Instant;

public record CommentResponseDTO(
        long id,
        Instant createdAt,
        Instant updatedAt,
        String body,
        AuthorDTO author
) {}
