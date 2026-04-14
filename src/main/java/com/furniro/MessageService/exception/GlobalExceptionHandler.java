package com.furniro.MessageService.exception;

import com.furniro.MessageService.dto.API.AType;
import com.furniro.MessageService.dto.API.ErrorType;
import com.furniro.MessageService.util.MailErrorCode;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MailException.class)
    public ResponseEntity<AType> handleUserException(MailException ex) {

        MailErrorCode code = ex.getErrorCode();

        AType error = ErrorType.builder()
                .code(code.getCode())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.valueOf(code.getCode()));
    }

}
