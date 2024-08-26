package com.conduit.application.dto.article;

import java.util.List;

public record ArticlesResponseDto(List<ArticleDto> articles, long articlesCount) {
}
