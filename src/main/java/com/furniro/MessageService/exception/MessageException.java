package com.furniro.MessageService.exception;

import com.furniro.MessageService.util.MessageErrorCode;

import lombok.Getter;

@Getter
public class MessageException extends BaseException {
        private final MessageErrorCode errorCode;

    public MessageException(MessageErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
