package com.subash.SGDisposals.exception;

import lombok.Data;

@Data
public class UnauthorizedRequestException extends RuntimeException{

    private final String message;

    public UnauthorizedRequestException(String message){
        this.message = message;
    }
}
