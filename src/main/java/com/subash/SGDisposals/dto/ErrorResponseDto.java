package com.subash.SGDisposals.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDto {

    private LocalDateTime timestamp;
    private HttpStatus status;
    private String error;
    private String message;
}
