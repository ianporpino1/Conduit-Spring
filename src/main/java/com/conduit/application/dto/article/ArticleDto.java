package com.conduit.application.dto.article;

import com.conduit.domain.model.Tag;

import java.time.Instant;
import java.util.List;

public record ArticleDto(String slug,
                         String title,
                         String description,
                         List<String> tagList,
                         Instant createdAt,
                         Instant updatedAt,
                         Boolean favorited,
                         int favoritesCount,
                         AuthorDto author) {
}
