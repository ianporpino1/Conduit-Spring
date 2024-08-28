package com.conduit.application.dto.article;

import java.time.Instant;
import java.util.List;

public record MultipleArticlesDto(String slug,
                                  String title,
                                  String description,
                                  List<String> tagList,
                                  Instant createdAt,
                                  Instant updatedAt,
                                  Boolean favorited,
                                  int favoritesCount,
                                  AuthorDto author) {
}
