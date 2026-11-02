package com.shopping_mall_api.global.exception;

public class InvalidTokenException extends GlobalShoppingMallException {
    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidTokenException(ErrorCode errorCode, String errorMessage){
        super(errorCode, errorMessage);
    }
}
