package com.conduit.application.dto.article;

import java.util.List;

public record ArticlesResponseDto(List<MultipleArticlesDTO> articles, long articlesCount) {
}
