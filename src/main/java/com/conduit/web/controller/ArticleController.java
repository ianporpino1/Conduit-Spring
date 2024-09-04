package com.conduit.web.controller;

import com.conduit.domain.model.Article;
import com.conduit.domain.model.Tag;
import com.conduit.domain.model.User;
import com.conduit.domain.repository.ArticleRepository;
import com.conduit.domain.repository.TagRepository;
import com.conduit.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/articles")
    @Transactional(readOnly = true)
    public List<Article> listArticles(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String favorited,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        Long authorId = null;
        Long tagId = null;

        if (author != null) {
            Optional<User> userOpt = userRepository.findUserByUsername(author);
            if (userOpt.isPresent()) {
                authorId = userOpt.get().getId();
            } else {
                // Tratar o caso onde o usuário não é encontrado
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
        }

        if (tag != null) {
            Optional<Tag> tagOpt = tagRepository.findByName(tag);
            if (tagOpt.isPresent()) {
                tagId = tagOpt.get().getId();
            } else {
                // Tratar o caso onde a tag não é encontrada
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found");
            }
        }
        

        return articleRepository.findAllArticlesByFilters(tagId, authorId, null, limit, offset);
    }
}
