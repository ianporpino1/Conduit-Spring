package com.conduit.application.dto.mapper;

import com.conduit.application.dto.article.ArticleResponseDTO;
import com.conduit.application.dto.article.AuthorDTO;
import com.conduit.application.exception.UserNotFoundException;
import com.conduit.domain.model.Article;
import com.conduit.domain.model.Tag;
import com.conduit.domain.model.User;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class ArticleMapper {

    public ArticleResponseDTO toDto(Article article, User author, Set<String> tagNames, boolean isFavorited, boolean isFollowing) {
        return new ArticleResponseDTO(
                article.getSlug(),
                article.getTitle(),
                article.getDescription(),
                article.getBody(),
                tagNames,
                article.getCreatedAt(),
                article.getUpdatedAt(),
                isFavorited,
                article.getFavoritedBy().size(),
                toAuthorDto(author, isFollowing)
        );
    }

    public AuthorDTO toAuthorDto(User author, boolean isFollowing) {
        return new AuthorDTO(
                author.getUsername(),
                author.getBio(),
                author.getImage(),
                isFollowing
        );
    }
}
