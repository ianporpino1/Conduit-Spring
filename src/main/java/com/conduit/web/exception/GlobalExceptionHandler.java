package com.conduit.web.exception;

import com.conduit.application.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SlugAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleSlugAlreadyExistsException(SlugAlreadyExistsException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleArticleNotFoundException(ArticleNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ArticleAlreadyFavoritedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleArticleAlreadyFavoritedException(ArticleAlreadyFavoritedException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ArticleAlreadyUnfavoritedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleArticleAlreadyUnfavoritedException(ArticleAlreadyUnfavoritedException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedArticleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleUnauthorizedArticleUpdateException(UnauthorizedArticleException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleUnauthorizedActionException(UnauthorizedActionException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleCommentNotFoundException(CommentNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }
    

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleInvalidInputException(InvalidInputException ex) {
        return new ErrorResponse(ex.getMessage());
    }
}

