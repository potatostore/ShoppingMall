package com.shopping_mall_api.global.exception;

public class PaymentException extends GlobalShoppingMallException{
    public PaymentException(ErrorCode errorCode){
        super(errorCode);
    }

    public PaymentException(ErrorCode errorCode,String message) {
        super(errorCode, message);
    }
}
