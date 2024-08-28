package com.conduit.application.dto.article;

import java.util.List;

public record CreateArticleDTO(String title, String description, String body, List<String> tagList) {
}
