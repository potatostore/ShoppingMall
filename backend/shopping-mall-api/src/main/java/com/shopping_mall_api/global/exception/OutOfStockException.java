package com.shopping_mall_api.global.exception;

public class OutOfStockException extends GlobalShoppingMallException {
    public OutOfStockException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OutOfStockException(ErrorCode errorCode, String errorMessage){
        super(errorCode, errorMessage);
    }
}
