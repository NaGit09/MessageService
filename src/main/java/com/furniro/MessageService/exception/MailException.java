package com.furniro.MessageService.exception;

import com.furniro.MessageService.util.MailErrorCode;

import lombok.Getter;

@Getter
public class MailException extends BaseException {
    private final MailErrorCode errorCode;

    public MailException(MailErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
