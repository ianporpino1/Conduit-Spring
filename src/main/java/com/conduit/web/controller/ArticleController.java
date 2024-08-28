package com.conduit.web.controller;

import com.conduit.application.dto.article.*;
import com.conduit.application.service.ArticleService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
public class ArticleController {
    private  final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/articles")
    public ArticlesResponseDto getArticles(@RequestParam(value = "tag", required = false)String tag,
                                           @RequestParam(value = "author", required = false) String author,
                                           @RequestParam(value = "favorited", required = false) String favorited,
                                           @RequestParam(value = "limit", defaultValue = "20") int limit,
                                           @RequestParam(value = "offset", defaultValue = "0") int offset,
                                           @AuthenticationPrincipal Jwt principal) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Order.desc("createdAt")));
        
        return articleService.listArticles(tag, author, favorited, pageable, principal);
    }
    
    @PostMapping("/articles")
    public ResponseEntity<Map<String, SingleArticleDto>> createArticle(@RequestBody ArticleRequestDto articleRequestDto,
                                                                          @AuthenticationPrincipal Jwt principal){
        SingleArticleDto articleDto = articleService.createArticle(articleRequestDto,principal);
        
        Map<String, SingleArticleDto> response = new HashMap<>();
        response.put("article", articleDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/articles/{slug}")
    public ResponseEntity<Map<String, SingleArticleDto>> getArticleFromSlug(@PathVariable String slug,
                                                                               @AuthenticationPrincipal Jwt principal){
        SingleArticleDto articleDto = articleService.getArticleFromSlug(slug,principal);
        
        Map<String, SingleArticleDto> response = new HashMap<>();
        response.put("article", articleDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/articles/feed")
    public ArticlesResponseDto getArticlesFeed(@RequestParam(value = "limit", defaultValue = "20") int limit,
                                               @RequestParam(value = "offset", defaultValue = "0") int offset,
                                               @AuthenticationPrincipal Jwt principal){
        Pageable pageable = PageRequest.of(offset / limit, limit, 
                Sort.by(Sort.Order.desc("createdAt")));
        
        return articleService.getFeedArticles(principal,pageable);
    }
    
    @PutMapping("/articles/{slug}")
    public ResponseEntity<Map<String, SingleArticleDto>> updateArticle(@PathVariable String slug,
                                                                          UpdateArticleDto updateArticleDto,
                                                                          @AuthenticationPrincipal Jwt principal){
        SingleArticleDto updatedArticleDto = articleService.updateArticle(slug, updateArticleDto, principal);

        Map<String, SingleArticleDto> response = new HashMap<>();
        response.put("article", updatedArticleDto);
        return ResponseEntity.ok(response);
    }
}
