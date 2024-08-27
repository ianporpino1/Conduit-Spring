package com.conduit.application.exception;

public class ArticleNotFoundException extends RuntimeException{
    public ArticleNotFoundException() {
        super("Article not found");
    }
}
