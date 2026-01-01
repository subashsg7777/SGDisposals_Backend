package com.subash.SGDisposals.exception;

import lombok.Data;

@Data
public class OrderException extends RuntimeException{

    private final String message;

    public OrderException(String message) {
        this.message = message;
    }
}
