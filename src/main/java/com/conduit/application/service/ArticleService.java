package com.conduit.application.service;

import com.conduit.application.dto.article.*;
import com.conduit.application.exception.*;
import com.conduit.domain.model.Article;
import com.conduit.domain.model.Tag;
import com.conduit.domain.model.User;
import com.conduit.domain.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public MultipleArticlesResponseDTO listArticles(String tag, String author, String favorited, Pageable pageable, Jwt currentUserJwt) {
        Page<Article> articlesPage = articleRepository.findArticles(tag, author, favorited, pageable);
        
        Long totalArticlesCount = articleRepository.countArticles(tag, author, favorited);

        Long currentUserId = Optional.ofNullable(currentUserJwt)
                .map(authenticationService::extractUserId)
                .orElse(null);

        Long filterUserId = Optional.ofNullable(favorited)
                .map(userService::getUserByUsername)
                .map(User::getId)
                .orElse(null);

        List<ArticleResponseDTO> multipleArticlesDTOS = articlesPage.getContent().stream()
                .map(article -> convertArticleToDto(article, filterUserId,currentUserId))
                .toList();
        
        return new MultipleArticlesResponseDTO(multipleArticlesDTOS, totalArticlesCount);
    }
    
    @Transactional
    public SingleArticleResponseDTO createArticle(ArticleRequestDTO articleRequestDto, Jwt currentUserJwt){
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User user = userService.getUserById(currentUserId);
        
        String slug = createSlug(articleRequestDto.title());

        articleRepository.findBySlug(slug)
                .ifPresent(article -> {
                    throw new SlugAlreadyExistsException(slug);
                });

        List<Tag> tags = Collections.emptyList();
        if(articleRequestDto.tagList() != null){
            tags = articleRequestDto.tagList().stream().map(tagService::createTagFromString).toList();
        }
        
        Article newArticle = new Article();
        newArticle.setAuthor(user);
        newArticle.setCreatedAt(Instant.now());
        newArticle.setUpdatedAt(Instant.now());
        newArticle.setBody(articleRequestDto.body());
        newArticle.setDescription(articleRequestDto.description());
        newArticle.setTitle(articleRequestDto.title());
        newArticle.setSlug(slug);
        newArticle.setTagList(tags);
        
        Article savedArticle = articleRepository.save(newArticle);

        return new SingleArticleResponseDTO(convertArticleToSingleDto(savedArticle, user.getId(), user.getId()));
    }

    private String createSlug(String title) {
        if (title == null) {
            return null;
        }
        String slug = title.toLowerCase();
        slug = slug.replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
        return slug;
    }
    
    @Transactional(readOnly = true)
    public MultipleArticlesResponseDTO getArticlesFeed(Jwt currentUserJwt, Pageable pageable) {
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        List<Long> followingIds = userService.getFollowing(currentUserId).stream().map(User::getId).toList();

        Page<Article> articlesPage = articleRepository.findArticlesByFollowingUsersIds(followingIds, pageable);
        
        Long articlesCount = articleRepository.countArticlesByFollowingUsers(followingIds);

        List<ArticleResponseDTO> multipleArticlesDTOS = articlesPage.getContent().stream()
                .map(article -> convertArticleToDto(article, currentUserId, currentUserId))
                .toList();

        return new MultipleArticlesResponseDTO(multipleArticlesDTOS, articlesCount);
    }
    
    @Transactional
    public SingleArticleResponseDTO updateArticle(String slug, ArticleRequestDTO updateArticleDto, Jwt currentUserJwt){
        Article oldArticle = articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        if(!Objects.equals(oldArticle.getAuthor().getId(), currentUserId)){
            throw new UnauthorizedArticleException("User is not the author of this article");
        }

        boolean isModified = updateArticleFields(oldArticle, updateArticleDto);
        if (isModified) {
            oldArticle.setUpdatedAt(Instant.now());
            articleRepository.save(oldArticle);
        }
        
        articleRepository.save(oldArticle);
        return new SingleArticleResponseDTO(convertArticleToSingleDto(oldArticle, currentUserId,currentUserId));
    }

    private boolean updateArticleFields(Article article, ArticleRequestDTO updateArticleDto) {
        boolean isModified = false;

        if (updateArticleDto.title() != null) {
            article.setTitle(updateArticleDto.title());
            article.setSlug(createSlug(updateArticleDto.title()));
            isModified = true;
        }

        if (updateArticleDto.description() != null) {
            article.setDescription(updateArticleDto.description());
            isModified = true;
        }

        if (updateArticleDto.body() != null) {
            article.setBody(updateArticleDto.body());
            isModified = true;
        }

        return isModified;
    }
    
    public SingleArticleResponseDTO getArticleFromSlug(String slug, Jwt currentUserJwt){
        Article article = articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);

        Long currentUserId = Optional.ofNullable(currentUserJwt)
                .map(authenticationService::extractUserId)
                .orElse(null);
        return new SingleArticleResponseDTO(convertArticleToSingleDto(article, currentUserId,currentUserId));
    }

    private ArticleResponseDTO convertArticleToDto(Article article, Long filterUserId, Long currentUserId) {
        boolean isFavorited = filterUserId != null && article.getFavoritedBy().stream()
                .anyMatch(user -> user.getId().equals(filterUserId));
        boolean isFollowing = currentUserId != null && article.getAuthor().getFollowedBy().stream()
                .anyMatch(follower -> follower.getId().equals(currentUserId));

        return new ArticleResponseDTO(
                article.getSlug(),
                article.getTitle(),
                article.getDescription(),
                article.getTagList().stream().map(Tag::getName).toList(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                isFavorited, 
                article.getFavoritedBy().size(), 
                convertAuthorToDto(article.getAuthor(), isFollowing) 
        );
    }
    
    private ArticleResponseDTO convertArticleToSingleDto(Article article, Long filterUserId, Long currentUserId) {
        boolean isFavorited = filterUserId != null && article.getFavoritedBy().stream()
                .anyMatch(user -> user.getId().equals(filterUserId));
        boolean isFollowing = currentUserId != null && article.getAuthor().getFollowedBy().stream()
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
                convertAuthorToDto(article.getAuthor(), isFollowing)
        );
    }

    public AuthorDTO convertAuthorToDto(User author, boolean isFollowing) {
        return new AuthorDTO(
                author.getUsername(),
                author.getBio(),
                author.getImage(),
                isFollowing
        );
    }
    @Transactional
    public void deleteArticle(String slug, Jwt currentUserJwt) {
        Article articleToBeDeleted = articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        if(!Objects.equals(articleToBeDeleted.getAuthor().getId(), currentUserId)){
            throw new UnauthorizedArticleException("User is not the author of this article");
        }
        
        articleRepository.delete(articleToBeDeleted);
    }
    
    @Transactional
    public SingleArticleResponseDTO addFavoriteArticle(String slug, Jwt currentUserJwt) {
        Article articleToBeFavorited = articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);
        
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userService.getUserById(currentUserId);
        
        if(currentUser.getFavoritedArticles().contains(articleToBeFavorited)){
            throw new ArticleAlreadyFavoritedException();
        }
        
        currentUser.getFavoritedArticles().add(articleToBeFavorited);
        articleToBeFavorited.getFavoritedBy().add(currentUser);
        
        articleRepository.save(articleToBeFavorited);
        
        return new SingleArticleResponseDTO(convertArticleToSingleDto(articleToBeFavorited, 
                currentUserId,
                currentUserId));
    }
    
    @Transactional
    public SingleArticleResponseDTO deleteFavoriteArticle(String slug, Jwt currentUserJwt) {
        Article articleToBeUnfavorited = articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);

        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userService.getUserById(currentUserId);

        if(!currentUser.getFavoritedArticles().contains(articleToBeUnfavorited)){
            throw new ArticleAlreadyUnfavoritedException();
        }
        currentUser.getFavoritedArticles().remove(articleToBeUnfavorited);
        articleToBeUnfavorited.getFavoritedBy().remove(currentUser);

        return new SingleArticleResponseDTO(convertArticleToSingleDto(articleToBeUnfavorited, 
                currentUserId,currentUserId));
    }

    public Article findArticleBySlug(String slug){
        return articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);
    }

    public Article save(Article article) {
        return articleRepository.save(article);
    }
}
