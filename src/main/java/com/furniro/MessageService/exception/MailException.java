package com.furniro.MessageService.exception;

import com.furniro.MessageService.util.MailErrorCode;

import lombok.Getter;

@Getter
public class MailException extends RuntimeException {
    private final MailErrorCode errorCode;

    public MailException(MailErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
