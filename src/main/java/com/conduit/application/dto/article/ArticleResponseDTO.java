package com.conduit.application.dto.article;

import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.time.Instant;
import java.util.List;

@JsonInclude(NON_NULL)
public record ArticleResponseDTO(
        String slug,
        String title,
        String description,
        String body,
        List<String> tagList,
        Instant createdAt,
        Instant updatedAt,
        Boolean favorited,
        int favoritesCount,
        AuthorDTO author
) {
    // Construtor para criar uma instância sem o campo body
    public ArticleResponseDTO(String slug, String title, String description, List<String> tagList,
                              Instant createdAt, Instant updatedAt, Boolean favorited,
                              int favoritesCount, AuthorDTO author) {
        this(slug, title, description, null, tagList, createdAt, updatedAt, favorited, favoritesCount, author);
    }
}
