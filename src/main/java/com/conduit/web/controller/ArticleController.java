package com.conduit.web.controller;

import com.conduit.application.dto.article.*;
import com.conduit.application.service.ArticleService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class ArticleController {
    private  final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/articles")
    public MultipleArticlesResponseDTO getArticles(@RequestParam(value = "tag", required = false)String tag,
                                           @RequestParam(value = "author", required = false) String author,
                                           @RequestParam(value = "favorited", required = false) String favorited,
                                           @RequestParam(value = "limit", defaultValue = "20") int limit,
                                           @RequestParam(value = "offset", defaultValue = "0") int offset,
                                           @AuthenticationPrincipal Jwt principal) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Order.desc("createdAt")));
        
        return articleService.listArticles(tag, author, favorited, pageable, principal);
    }
    
    @PostMapping("/articles")
    public SingleArticleResponseDTO createArticle(@RequestBody ArticleRequestDTO articleRequestDto,
                                            @AuthenticationPrincipal Jwt principal){
        return articleService.createArticle(articleRequestDto,principal);
    }
    
    @GetMapping("/articles/{slug}")
    public SingleArticleResponseDTO getArticleFromSlug(@PathVariable String slug,
                                                 @AuthenticationPrincipal Jwt principal){
        return articleService.getArticleFromSlug(slug,principal);
    }
    
    @GetMapping("/articles/feed")
    public MultipleArticlesResponseDTO getArticlesFeed(@RequestParam(value = "limit", defaultValue = "20") int limit,
                                               @RequestParam(value = "offset", defaultValue = "0") int offset,
                                               @AuthenticationPrincipal Jwt principal){
        Pageable pageable = PageRequest.of(offset / limit, limit, 
                Sort.by(Sort.Order.desc("createdAt")));
        
        return articleService.getArticlesFeed(principal,pageable);
    }
    
    @PutMapping("/articles/{slug}")
    public SingleArticleResponseDTO updateArticle(@PathVariable String slug,
                                            ArticleRequestDTO updateArticleDto,
                                            @AuthenticationPrincipal Jwt principal){
        return articleService.updateArticle(slug, updateArticleDto, principal);
    }
    
    @DeleteMapping("/articles/{slug}")
    public void deleteArticle(@PathVariable String slug, @AuthenticationPrincipal Jwt principal){
        articleService.deleteArticle(slug, principal);
    }
    
    @PostMapping("/articles/{slug}/favorite")
    public SingleArticleResponseDTO favoriteArticle(@PathVariable String slug,
                                              @AuthenticationPrincipal Jwt principal){
        return articleService.addFavoriteArticle(slug,principal);
    }

    @DeleteMapping("/articles/{slug}/favorite")
    public SingleArticleResponseDTO unfavoriteArticle(@PathVariable String slug,
                                                @AuthenticationPrincipal Jwt principal){
        return articleService.deleteFavoriteArticle(slug,principal);
    }
    
    
}
