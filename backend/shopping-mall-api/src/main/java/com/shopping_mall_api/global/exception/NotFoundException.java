package com.shopping_mall_api.global.exception;

public class NotFoundException extends GlobalShoppingMallException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode, String errorMessage){
        super(errorCode, errorMessage);
    }
}
