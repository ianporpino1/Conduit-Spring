package com.conduit.application.exception;

public class ArticleAlreadyUnfavoritedException extends RuntimeException {
    public ArticleAlreadyUnfavoritedException() {
        super("User already unfavorited this article");
    }
}
