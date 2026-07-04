package com.furniro.MessageService.exception;

import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.dto.API.ErrorType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<AType> handleBaseException(CustomException ex) {
        return ResponseEntity.status(ex.getErrorCode().getCode())
                .body(ex.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AType> handleException(Exception ex) {

        AType error = ErrorType.builder()
                .code(500)
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();

        return ResponseEntity.status(500)
                .body(error);
    }

}
