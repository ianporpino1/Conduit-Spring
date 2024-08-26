package com.conduit.application.dto.article;

import java.util.List;

public record CreateArticleDto(String title, String description, String body, List<String> tagList) {
}
