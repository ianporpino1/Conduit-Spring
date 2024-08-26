package com.conduit.application.service;

import com.conduit.application.dto.article.ArticleDto;
import com.conduit.application.dto.article.ArticlesResponseDto;
import com.conduit.application.dto.article.AuthorDto;
import com.conduit.domain.model.Article;
import com.conduit.domain.model.Tag;
import com.conduit.domain.model.User;
import com.conduit.domain.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private  final AuthenticationService authenticationService;
    
    public ArticleService(ArticleRepository articleRepository, AuthenticationService authenticationService) {
        this.articleRepository = articleRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional(readOnly = true)
    public ArticlesResponseDto listArticles(String tag, String author, String favorited, Pageable pageable, Jwt currentUserJwt) {
        Page<Article> articlesPage = articleRepository.findArticles(tag, author, favorited, pageable);
        
        Long totalArticlesCount = articleRepository.countArticles(tag, author, favorited);

        Long currentUserId;
        if(currentUserJwt != null) {
            currentUserId = authenticationService.extractUserId(currentUserJwt);
        } else {
            currentUserId = null;
        }


        String filterUser = favorited != null ? favorited : "";

        List<ArticleDto> articleDtos = articlesPage.getContent().stream()
                .map(article -> convertToDto(article, filterUser,currentUserId)) 
                .collect(Collectors.toList());
        
        return new ArticlesResponseDto(articleDtos, totalArticlesCount);
    }

    private ArticleDto convertToDto(Article article, String filterUser, Long currentUserId) {
        boolean isFavorited = filterUser.isEmpty() && article.getFavoritedBy().stream()
                .anyMatch(user -> user.getUsername().equals(filterUser));
        boolean isFollowing = currentUserId != null && article.getAuthor().getFollowedBy().stream()
                .anyMatch(follower -> follower.getId().equals(currentUserId));

        return new ArticleDto(
                article.getSlug(),
                article.getTitle(),
                article.getDescription(),
                article.getTagList().stream().map(Tag::getName).collect(Collectors.toList()),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                isFavorited, 
                article.getFavoritedBy().size(), 
                convertAuthorToDto(article.getAuthor(), isFollowing) 
        );
    }

    private AuthorDto convertAuthorToDto(User author, boolean isFollowing) {
        return new AuthorDto(
                author.getUsername(),
                author.getBio(),
                author.getImage(),
                isFollowing
        );
    }
}
