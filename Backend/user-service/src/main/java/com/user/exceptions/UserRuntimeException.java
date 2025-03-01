package com.user.exceptions;

import lombok.Getter;

@Getter
public class UserRuntimeException extends RuntimeException{

    private String reason;

    public UserRuntimeException(String reason) {
        super(reason);
        this.reason = reason;
    }
}
