package com.conduit.application.exception;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException() {
        super("Comment not found");
    }
}
