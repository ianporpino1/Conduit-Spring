package com.conduit.application.service;

import com.conduit.application.dto.article.ArticleDto;
import com.conduit.application.dto.article.ArticleRequestDto;
import com.conduit.application.dto.article.ArticlesResponseDto;
import com.conduit.application.dto.article.AuthorDto;
import com.conduit.application.exception.ArticleNotFoundException;
import com.conduit.application.exception.SlugAlreadyExistsException;
import com.conduit.domain.model.Article;
import com.conduit.domain.model.Tag;
import com.conduit.domain.model.User;
import com.conduit.domain.repository.ArticleRepository;
import com.conduit.domain.repository.TagRepository;
import com.conduit.domain.repository.UserRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private  final AuthenticationService authenticationService;
    private final UserService userService;
    private final TagService tagService;

    public ArticleService(ArticleRepository articleRepository, AuthenticationService authenticationService, UserService userService, TagService tagService) {
        this.articleRepository = articleRepository;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.tagService = tagService;
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

        Long filterUserId;
        if(favorited != null){
            filterUserId = userService.getUserByUsername(favorited).getId();
        }else {
            filterUserId = null;
        }

        List<ArticleDto> articleDtos = articlesPage.getContent().stream()
                .map(article -> convertArticleToDto(article, filterUserId,currentUserId)) 
                .collect(Collectors.toList());
        
        return new ArticlesResponseDto(articleDtos, totalArticlesCount);
    }
    
    public ArticleDto createArticle(ArticleRequestDto articleRequestDto, Jwt currentUserJwt){
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User user = userService.getUserById(currentUserId);
        
        String slug = createSlug(articleRequestDto.article().title());

        articleRepository.findBySlug(slug)
                .ifPresent(article -> {
                    throw new SlugAlreadyExistsException(slug);
                });

        List<Tag> tags = Collections.emptyList();
        if(articleRequestDto.article().tagList() != null){
            tags = articleRequestDto.article().tagList().stream().map(tagService::createTagFromString).toList();
        }
        
        Article newArticle = new Article();
        newArticle.setAuthor(user);
        newArticle.setCreatedAt(Instant.now());
        newArticle.setUpdatedAt(Instant.now());
        newArticle.setBody(articleRequestDto.article().body());
        newArticle.setDescription(articleRequestDto.article().description());
        newArticle.setTitle(articleRequestDto.article().title());
        newArticle.setSlug(slug);
        newArticle.setTagList(tags);
        
        Article savedArticle = articleRepository.save(newArticle);
        
        boolean isFollowing = false;
        convertAuthorToDto(user, isFollowing);

        return convertArticleToDto(savedArticle, user.getId(), user.getId());
    }

    private String createSlug(String title) {
        if (title == null) {
            return null;
        }
        String slug = title.toLowerCase();
        slug = slug.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
        return slug;
    }

    public ArticlesResponseDto getFeedArticles(Jwt currentUserJwt, Pageable pageable) {
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        List<Long> followingIds = userService.getFollowing(currentUserId).stream().map(User::getId).toList();

        Page<Article> articlesPage = articleRepository.findArticlesByFollowingUsersIds(followingIds, pageable);
        
        Long articlesCount = articleRepository.countArticlesByFollowingUsers(followingIds);

        List<ArticleDto> articleDtos = articlesPage.getContent().stream()
                .map(article -> convertArticleToDto(article, currentUserId, currentUserId))
                .toList();

        return new ArticlesResponseDto(articleDtos, articlesCount);
    }
    
    public ArticleDto getArticleFromSlug(String slug, Jwt currentUserJwt){
        Long currentUserId;
        if(currentUserJwt != null) {
            currentUserId = authenticationService.extractUserId(currentUserJwt);
        } else {
            currentUserId = null;
        }
        
        Article article = articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);
        return convertArticleToDto(article, currentUserId,currentUserId);
    }

    private ArticleDto convertArticleToDto(Article article, @Nullable Long filterUserId, Long currentUserId) {
        boolean isFavorited = filterUserId != null && article.getFavoritedBy().stream()
                .anyMatch(user -> user.getId().equals(filterUserId));
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
