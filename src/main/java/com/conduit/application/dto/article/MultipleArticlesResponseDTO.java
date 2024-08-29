package com.conduit.application.dto.article;

import java.util.List;

public record MultipleArticlesResponseDTO(List<ArticleResponseDTO> articles, long articlesCount) {
}
