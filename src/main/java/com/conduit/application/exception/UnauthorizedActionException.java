package com.conduit.application.exception;

public class UnauthorizedActionException  extends RuntimeException{
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
