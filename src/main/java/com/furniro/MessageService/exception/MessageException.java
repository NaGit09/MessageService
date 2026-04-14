package com.furniro.MessageService.exception;

import com.furniro.MessageService.util.MessageErrorCode;

import lombok.Getter;

@Getter
public class MessageException extends  RuntimeException {
        private final MessageErrorCode errorCode;

    public MessageException(MessageErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
