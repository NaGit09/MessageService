package com.furniro.MessageService.exception;

import com.furniro.MessageService.util.PromotionErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionException extends BaseException {
    private final PromotionErrorCode errorCode;

    public PromotionException(PromotionErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
