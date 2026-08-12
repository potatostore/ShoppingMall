package com.shopping_mall_api.global.exception;

public class UnmatchedPriceException extends GlobalShoppingMallException {
    public UnmatchedPriceException(ErrorCode errorCode){
        super(errorCode);
    }

    public UnmatchedPriceException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
