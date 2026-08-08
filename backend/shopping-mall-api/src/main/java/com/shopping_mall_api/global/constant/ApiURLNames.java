package com.shopping_mall_api.global.constant;

public class ApiURLNames {
    // users api
    public static final String userURL = "/users";
    public static final String createUserURL = "";
    public static final String findUsersURL = "";
    public static final String findUserURL = "/{userId}";
    public static final String updateUserURL = "/{userId}";
    public static final String deleteUserURL = "/{userId}";

    // carts api
    public static final String cartURL = "/carts";
    public static final String findCartsURL = "";
    public static final String findCartURL = "/{userId}";
    public static final String updateCartURL = "/{userId}";
    public static final String deleteCartURL = "/{userId}";
    public static final String deleteCartItemInCartURL = "/{userId}/items/{productId}";

    // products api
    public static final String productURL = "/products";
    public static final String createProductURL = "";
    public static final String findProductsURL = "";
    public static final String findProductURL = "/{productId}";
    public static final String updateProductURL = "/{productId}";
    public static final String deleteProductURL = "/{productId}";

    // orders api
    public static final String orderURL = "/orders";
    public static final String createOrderURL = "/{userId}";
    public static final String findOrdersURL = "";
    public static final String findOrdersWithUserIdURL = "/user/{userId}";
    public static final String findOrderURL = "/{orderId}";
    public static final String updateOrderURL = "/{orderId}";
    public static final String deleteOrderURL = "/{orderId}";

    // toss payments api
    public static final String tossPaymentsRequestURL = "https://api.tosspayments.com/v1/payments/confirm";
}
