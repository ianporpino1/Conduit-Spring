package com.conduit.web.controller;

import com.conduit.application.dto.comment.CommentRequestWrapperDTO;
import com.conduit.application.dto.comment.MultipleCommentsResponseDTO;
import com.conduit.application.dto.comment.SingleCommentResponseDTO;
import com.conduit.application.service.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/articles/{slug}/comments")
    public SingleCommentResponseDTO createComment(@PathVariable String slug,
                                                  @RequestBody CommentRequestWrapperDTO commentRequestDTO, 
                                                  @AuthenticationPrincipal Jwt principal){
        return commentService.createComment(slug, commentRequestDTO.comment(),principal);
    }
    @GetMapping("/articles/{slug}/comments")
    public MultipleCommentsResponseDTO getAllComments(@PathVariable String slug,
                                                      @AuthenticationPrincipal Jwt principal){
        return commentService.getAllComments(slug, principal);
    }
    @DeleteMapping("/articles/{slug}/comments/{id}")
    public void deleteComment(@PathVariable String slug,
                              @PathVariable Long id,
                              @AuthenticationPrincipal Jwt principal){
        commentService.deleteComment(slug,id,principal);
    }
}
