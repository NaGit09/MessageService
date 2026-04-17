package com.furniro.MessageService.exception;

import com.furniro.MessageService.util.NotificationErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifyException extends BaseException {
    private final NotificationErrorCode errorCode;

    public NotifyException(NotificationErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
