package org.develop.rest.users.exceptions;

public abstract class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}