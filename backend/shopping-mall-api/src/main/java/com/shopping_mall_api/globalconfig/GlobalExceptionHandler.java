package com.shopping_mall_api.globalconfig;

import org.springframework.web.bind.annotation.ExceptionHandler;

@ExceptionHandler
public class GlobalExceptionHandler extends RuntimeException {
    public GlobalExceptionHandler(String message) {
        super(message);
    }
}
