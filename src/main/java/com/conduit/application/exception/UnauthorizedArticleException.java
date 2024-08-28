package com.conduit.application.exception;

public class UnauthorizedArticleException extends RuntimeException{
    public UnauthorizedArticleException(String message) {
        super(message);
    }
}
