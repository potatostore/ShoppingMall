package com.shopping_mall_api.global.exception;

public class AlreadyExistException extends GlobalShoppingMallException {
    public AlreadyExistException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AlreadyExistException(ErrorCode errorCode, String errorMessage){
        super(errorCode, errorMessage);
    }
}
