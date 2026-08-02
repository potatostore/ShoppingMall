package com.shopping_mall_api.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "CM000", "Invalid input value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CM001", "Server error"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "U002", "User already exist"),

    //Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "Cannot found product"),
    PRODUCT_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "P002", "Product Already Exist"),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "P003", "Product quantity is less than 1"),

    //Cart
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "Cannot found cart"),
    CART_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "C002", "Cart Already Exist"),
    CART_EMPTY(HttpStatus.BAD_REQUEST, "C003", "There are no CartItem in Cart"),

    //Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "Cannot found Order");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}
