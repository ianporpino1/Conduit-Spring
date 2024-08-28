package com.conduit.application.dto.article;

import java.time.Instant;
import java.util.List;

public record SingleArticleDto(String slug,
                               String title,
                               String description,
                               String body,
                               List<String> tagList,
                               Instant createdAt,
                               Instant updatedAt,
                               Boolean favorited,
                               int favoritesCount,
                               AuthorDto author) {
}
