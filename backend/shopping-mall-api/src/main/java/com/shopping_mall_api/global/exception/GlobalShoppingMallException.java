package com.shopping_mall_api.global.exception;

import lombok.Getter;

@Getter
public class GlobalShoppingMallException extends RuntimeException{
    private final ErrorCode errorCode;

    public GlobalShoppingMallException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public GlobalShoppingMallException(ErrorCode errorCode, String errorMessage){
        super(errorMessage);
        this.errorCode = errorCode;
    }
}
