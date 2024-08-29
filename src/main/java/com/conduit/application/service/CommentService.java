package com.conduit.application.service;

import com.conduit.application.dto.comment.CommentRequestDTO;
import com.conduit.application.dto.comment.CommentResponseDTO;
import com.conduit.application.dto.comment.MultipleCommentsResponseDTO;
import com.conduit.application.dto.comment.SingleCommentResponseDTO;
import com.conduit.application.exception.CommentNotFoundException;
import com.conduit.application.exception.InvalidInputException;
import com.conduit.application.exception.UnauthorizedActionException;
import com.conduit.domain.model.Article;
import com.conduit.domain.model.Comment;
import com.conduit.domain.model.User;

import com.conduit.domain.repository.CommentRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    public SingleCommentResponseDTO createComment(String slug, 
                                                  CommentRequestDTO commentRequestDTO, 
                                                  Jwt currentUserJwt){
        if(commentRequestDTO.body().isBlank()){
            throw new InvalidInputException("comment cannot be empty");
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

        CommentResponseDTO commentDTO = convertCommentToDTO(savedComment, currentUserId);
        
        return new SingleCommentResponseDTO(commentDTO);
    }
    
    private CommentResponseDTO convertCommentToDTO(Comment comment, Long currentUserId){
        boolean isFollowing = currentUserId != null && comment.getAuthor().getFollowedBy().stream()
                .anyMatch(follower -> follower.getId().equals(currentUserId));
        
        return new CommentResponseDTO(
                comment.getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getBody(),
                articleService.convertAuthorToDto(comment.getAuthor(),isFollowing)
        );
    }

    public MultipleCommentsResponseDTO getAllComments(String slug, Jwt currentUserJwt) {
        Long currentUserId = Optional.ofNullable(currentUserJwt)
                .map(authenticationService::extractUserId)
                .orElse(null);
        
        List<CommentResponseDTO> commentDTOS =  articleService.findArticleBySlug(slug).getComments().stream()
                .map(comment -> convertCommentToDTO(comment, currentUserId))
                .toList();
        return new MultipleCommentsResponseDTO(commentDTOS);
    }
    
    @Transactional
    public void deleteComment(String slug, Long id, Jwt currentUserJwt) {
        Comment commentToBeDeleted = commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);

        Long currentUserId = authenticationService.extractUserId(currentUserJwt);
        User user = userService.getUserById(currentUserId);
        
        if (!commentToBeDeleted.getAuthor().getUsername().equals(user.getUsername())) {
            throw new UnauthorizedActionException("Can't delete. User is not the author of the comment");
        }
        
        commentRepository.delete(commentToBeDeleted);
    }
}
