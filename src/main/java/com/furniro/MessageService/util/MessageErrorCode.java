package com.furniro.MessageService.util;

import lombok.Getter;

@Getter
public enum MessageErrorCode {
    MESSAGE_FAILED(400, "Mail failed"),
    MESSAGE_NOT_FOUND(404, "Mail not found"),
    MESSAGE_ALREADY_EXISTS(409, "Mail already exists"),
    MESSAGE_DELETE_FAILED(400, "Mail delete failed"),
    MESSAGE_UPDATE_FAILED(400, "Mail update failed");

    private final int code;
    private final String message;

    MessageErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
