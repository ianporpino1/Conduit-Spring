package com.conduit.application.service;

import com.conduit.application.dto.article.*;
import com.conduit.application.dto.mapper.ArticleMapper;
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
    private final ArticleMapper articleMapper;

    public ArticleService(ArticleRepository articleRepository, AuthenticationService authenticationService, UserService userService, TagService tagService, ArticleMapper articleMapper) {
        this.articleRepository = articleRepository;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.tagService = tagService;
        this.articleMapper = articleMapper;
    }

    @Transactional(readOnly = true)
    public MultipleArticlesResponseDTO listArticles(String tag, String author, String favorited, Pageable pageable, Jwt currentUserJwt) {
        Page<Article> articlesPage = articleRepository.findArticles(tag, author, favorited, pageable);
        
        Long totalArticlesCount = articleRepository.countArticles(tag, author, favorited);

        User currentUser = Optional.ofNullable(currentUserJwt)
                .map(authenticationService::extractUserId)
                .map(userService::getUserById)
                .orElse(null);
        

        List<ArticleResponseDTO> multipleArticlesDTOS = articlesPage.getContent().stream()
                .map(article -> articleMapper.toDto(article,currentUser))
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

        return new SingleArticleResponseDTO(articleMapper.toDto(savedArticle, user));
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
        User user = userService.getUserById(currentUserId);
        List<Long> followingIds = user.getFollowing().stream().map(User::getId).toList();

        Page<Article> articlesPage = articleRepository.findArticlesByFollowingUsersIds(followingIds, pageable);
        
        Long articlesCount = articleRepository.countArticlesByFollowingUsers(followingIds);

        List<ArticleResponseDTO> multipleArticlesDTOS = articlesPage.getContent().stream()
                .map(article -> articleMapper.toDto(article, user))
                .toList();

        return new MultipleArticlesResponseDTO(multipleArticlesDTOS, articlesCount);
    }
    
    @Transactional
    public SingleArticleResponseDTO updateArticle(String slug, ArticleRequestDTO updateArticleDto, Jwt currentUserJwt){
        Article oldArticle = articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User user = userService.getUserById(currentUserId);
        if(!Objects.equals(oldArticle.getAuthor().getId(), currentUserId)){
            throw new UnauthorizedArticleException("User is not the author of this article");
        }
        
        oldArticle.update(updateArticleDto.title(), updateArticleDto.description(), updateArticleDto.body());
//        articleRepository.save(oldArticle);
        return new SingleArticleResponseDTO(articleMapper.toDto(oldArticle,user));
    }
    
    
    public SingleArticleResponseDTO getArticleFromSlug(String slug, Jwt currentUserJwt){
        Article article = articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);

        User currentUser = Optional.ofNullable(currentUserJwt)
                .map(authenticationService::extractUserId)
                .map(userService::getUserById)
                .orElse(null);
        return new SingleArticleResponseDTO(articleMapper.toDto(article,currentUser));
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

        if (articleToBeFavorited.isFavoritedBy(currentUser)) {
            throw new ArticleAlreadyFavoritedException();
        }

        articleToBeFavorited.addFavorite(currentUser);
//        articleRepository.save(articleToBeFavorited); //nao eh necessario, JPA ja cuida da atualizacao automatica
        
        return new SingleArticleResponseDTO(articleMapper.toDto(articleToBeFavorited, currentUser));
    }
    
    @Transactional
    public SingleArticleResponseDTO deleteFavoriteArticle(String slug, Jwt currentUserJwt) {
        Article articleToBeUnfavorited = articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);

        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User currentUser = userService.getUserById(currentUserId);

        if (!articleToBeUnfavorited.isFavoritedBy(currentUser)) {
            throw new ArticleAlreadyUnfavoritedException();
        }
        articleToBeUnfavorited.removeFavorite(currentUser);

        return new SingleArticleResponseDTO(articleMapper.toDto(articleToBeUnfavorited, currentUser));
    }

    public Article findArticleBySlug(String slug){
        return articleRepository.findArticleBySlug(slug)
                .orElseThrow(ArticleNotFoundException::new);
    }

    public Article save(Article article) {
        return articleRepository.save(article);
    }

    public AuthorDTO convertAuthorToDto(User author, boolean isFollowing) {
        return new AuthorDTO(author.getUsername(),author.getBio(),author.getImage(),isFollowing);
    }
}
