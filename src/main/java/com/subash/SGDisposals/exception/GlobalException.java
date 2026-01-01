package com.subash.SGDisposals.exception;

import com.subash.SGDisposals.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalException {

public ResponseEntity<ErrorResponseDto> globalException(Exception ex){

    ErrorResponseDto errorResponseDto = new ErrorResponseDto();
    errorResponseDto.setTimestamp(LocalDateTime.now());
    errorResponseDto.setMessage(ex.getMessage());
    errorResponseDto.setError("An error occurred");
    errorResponseDto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
}

@ExceptionHandler(InvalidRequestStateException.class)
    public ResponseEntity<ErrorResponseDto> invalidRequestStateException(InvalidRequestStateException ex){

    ErrorResponseDto errorResponseDto = new ErrorResponseDto();
    errorResponseDto.setMessage(ex.getMessage());
    errorResponseDto.setError("Invalid request state Exception");
    errorResponseDto.setStatus(HttpStatus.BAD_REQUEST);
    errorResponseDto.setTimestamp(LocalDateTime.now());
    return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
}

@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> resourceNotFoundException(ResourceNotFoundException ex){
    ErrorResponseDto errorResponseDto = new ErrorResponseDto();
    errorResponseDto.setTimestamp(LocalDateTime.now());
    errorResponseDto.setMessage(ex.getMessage());
    errorResponseDto.setError("Resource Not Found Exception");
    errorResponseDto.setStatus(HttpStatus.NOT_FOUND);
    return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
}

@ExceptionHandler(UnauthorizedRequestException.class)
    public ResponseEntity<ErrorResponseDto> unauthorizedRequestException(UnauthorizedRequestException ex){
    ErrorResponseDto errorResponseDto = new ErrorResponseDto();
    errorResponseDto.setTimestamp(LocalDateTime.now());
    errorResponseDto.setMessage(ex.getMessage());
    errorResponseDto.setError("Unauthorized Request");
    errorResponseDto.setStatus(HttpStatus.UNAUTHORIZED);
    return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDto);
}

@ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorResponseDto> orderException(OrderException ex){
    ErrorResponseDto errorResponseDto = new ErrorResponseDto();
    errorResponseDto.setTimestamp(LocalDateTime.now());
    errorResponseDto.setMessage(ex.getMessage());
    errorResponseDto.setError("Order Exception");
    errorResponseDto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
}
}
