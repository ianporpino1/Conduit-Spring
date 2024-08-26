package com.conduit.web.controller;

import com.conduit.application.dto.article.ArticlesResponseDto;
import com.conduit.application.service.ArticleService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
}
