package com.shopping_mall_api.global.exception;

public class CannotUpdateInfoException extends GlobalShoppingMallException {
    public CannotUpdateInfoException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CannotUpdateInfoException(ErrorCode errorCode, String errorMessage){
        super(errorCode, errorMessage);
    }
}
