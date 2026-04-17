package com.furniro.MessageService.exception;

import com.furniro.MessageService.util.SubscriptionErrorCode;

import lombok.Getter;

@Getter
public class SubscriptionException extends BaseException {
    private final SubscriptionErrorCode subscriptionErrorCode;

    public SubscriptionException(SubscriptionErrorCode subscriptionErrorCode) {
        super(subscriptionErrorCode.getCode(), subscriptionErrorCode.getMessage());
        this.subscriptionErrorCode = subscriptionErrorCode;
    }
}
