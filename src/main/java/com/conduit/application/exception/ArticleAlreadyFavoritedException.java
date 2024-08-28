package com.conduit.application.exception;

public class ArticleAlreadyFavoritedException extends RuntimeException{
    public ArticleAlreadyFavoritedException() {
        super("User already favorited this article");
    }
}
