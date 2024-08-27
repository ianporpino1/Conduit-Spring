package com.conduit.application.exception;

public class SlugAlreadyExistsException extends RuntimeException {
    public SlugAlreadyExistsException(String slug) {
        super("The title '" + slug + "' is already taken. Choose another title");
    }
}
