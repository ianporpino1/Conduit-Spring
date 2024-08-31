package com.conduit.application.dto.mapper;

import com.conduit.application.dto.article.ArticleResponseDTO;
import com.conduit.application.dto.article.AuthorDTO;
import com.conduit.domain.model.Article;
import com.conduit.domain.model.Tag;
import com.conduit.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {

    public ArticleResponseDTO toDto(Article article, Long currentUserId) {
        boolean isFavorited = article.getFavoritedBy().stream()
                .anyMatch(user -> user.getId().equals(currentUserId));
        boolean isFollowing = article.getAuthor().getFollowedBy().stream()
                .anyMatch(follower -> follower.getId().equals(currentUserId));

        return new ArticleResponseDTO(
                article.getSlug(),
                article.getTitle(),
                article.getDescription(),
                article.getBody(),
                article.getTagList().stream().map(Tag::getName).toList(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                isFavorited,
                article.getFavoritedBy().size(),
                toAuthorDto(article.getAuthor(), isFollowing)
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
