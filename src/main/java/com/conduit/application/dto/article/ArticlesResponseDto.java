package com.conduit.application.dto.article;

import java.util.List;

public record ArticlesResponseDto(List<MultipleArticlesDto> articles, long articlesCount) {
}
