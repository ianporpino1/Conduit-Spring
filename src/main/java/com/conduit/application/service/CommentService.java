package com.conduit.application.service;

import com.conduit.application.dto.comment.CommentDTO;
import com.conduit.application.dto.comment.CommentRequestDTO;
import com.conduit.application.dto.comment.MultipleCommentsResponseDTO;
import com.conduit.application.dto.comment.SingleCommentResponseDTO;
import com.conduit.application.exception.SlugAlreadyExistsException;
import com.conduit.domain.model.Article;
import com.conduit.domain.model.Comment;
import com.conduit.domain.model.User;

import com.conduit.domain.repository.CommentRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class CommentService {
    private  final ArticleService articleService;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CommentRepository commentRepository;

    public CommentService(ArticleService articleService, AuthenticationService authenticationService, UserService userService, CommentRepository commentRepository) {
        this.articleService = articleService;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public SingleCommentResponseDTO createComment(String slug, CommentRequestDTO commentRequestDTO, Jwt currentUserJwt){
        if(commentRequestDTO == null){
            throw new RuntimeException("comment cant be null");
        }
        
        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User user = userService.getUserById(currentUserId);

        Article article = articleService.findArticleBySlug(slug);

        Comment comment = new Comment();
        comment.setBody(commentRequestDTO.body());
        comment.setArticle(article);
        comment.setAuthor(user);
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        
        article.getComments().add(comment);
        user.getComments().add(comment);

        Comment savedComment = commentRepository.save(comment);
        Article savedArticle = articleService.save(article);
        userService.save(user);

        CommentDTO commentDTO = convertCommentToDTO(savedComment, currentUserId);
        
        return new SingleCommentResponseDTO(commentDTO);
    }
    
    private CommentDTO convertCommentToDTO(Comment comment, Long currentUserId){
        boolean isFollowing = currentUserId != null && comment.getAuthor().getFollowedBy().stream()
                .anyMatch(follower -> follower.getId().equals(currentUserId));
        
        return new CommentDTO(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getBody(),
                articleService.convertAuthorToDto(comment.getAuthor(),isFollowing)
        );
    }

    public MultipleCommentsResponseDTO getAllComments(String slug, Jwt currentUserJwt) {
        Long currentUserId;
        if(currentUserJwt != null) {
            currentUserId = authenticationService.extractUserId(currentUserJwt);
        } else {
            currentUserId = null;
        }
        
        List<CommentDTO> commentDTOS =  articleService.findArticleBySlug(slug).getComments().stream()
                .map(comment -> convertCommentToDTO(comment, currentUserId)).toList();
        return new MultipleCommentsResponseDTO(commentDTOS);
    }
    
    @Transactional
    public void deleteComment(String slug, Long id, Jwt currentUserJwt) {
        Comment commentToBeDeleted = commentRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Comment not found"));

        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User user = userService.getUserById(currentUserId);
        
        if (!commentToBeDeleted.getAuthor().getUsername().equals(user.getUsername())) {
            throw new RuntimeException("Can't delete. User is not the author of the comment");
        }
        
        commentRepository.delete(commentToBeDeleted);
        
    }
}
