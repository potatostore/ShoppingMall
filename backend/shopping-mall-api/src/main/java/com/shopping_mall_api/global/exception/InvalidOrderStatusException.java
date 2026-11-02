package com.shopping_mall_api.global.exception;

public class InvalidOrderStatusException extends GlobalShoppingMallException {
    public InvalidOrderStatusException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidOrderStatusException(ErrorCode errorCode, String errorMessage){
        super(errorCode, errorMessage);
    }
}
