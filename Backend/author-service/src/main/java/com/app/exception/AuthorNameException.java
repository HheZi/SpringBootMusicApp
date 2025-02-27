package com.app.exception;

import lombok.Getter;

@Getter
public class AuthorNameException extends RuntimeException {

    private final String reason;

    public AuthorNameException(String reason) {
        super(reason);
        this.reason = reason;
    }
}
