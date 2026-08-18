package com.shopping_mall_api.global.exception;

import com.shopping_mall_api.ShoppingMallApiApplication;

public class UnmatchedPasswordException extends GlobalShoppingMallException {
    public UnmatchedPasswordException(ErrorCode errorCode){
        super(errorCode);
    }

    public UnmatchedPasswordException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
