package com.subash.SGDisposals.exception;

import lombok.Data;

@Data
public class InvalidRequestStateException extends RuntimeException{
    private final String message;

    public InvalidRequestStateException(String message) {
        this.message = message;
    }
}
