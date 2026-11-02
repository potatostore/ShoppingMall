package com.shopping_mall_api.global.constant;

public class ApiURLNames {
    // users api
    public static final String userURL = "/users";
    public static final String createUserURL = "/signup";
    public static final String findUsersURL = "";
    public static final String findUserURL = "/me";
    public static final String updateUserURL = "/me";
    public static final String deleteUserURL = "/me";

    // carts api
    public static final String cartURL = "/carts";
    public static final String addCartItemInCartURL = "";
    public static final String findCartsURL = "";
    public static final String findCartURL = "/me";
    public static final String updateCartURL = "/me";
    public static final String deleteCartURL = "/me";
    public static final String deleteCartItemInCartURL = "/items/{productId}";

    // products api
    public static final String productURL = "/products";
    public static final String createProductURL = "";
    public static final String findProductsURL = "";
    public static final String findProductURL = "/{productId}";
    public static final String updateProductURL = "/{productId}";
    public static final String deleteProductURL = "/{productId}";

    // orders api
    public static final String orderURL = "/orders";
    public static final String createOrderURL = "";
    public static final String findOrdersURL = "";
    public static final String findOrdersWithUserIdURL = "/me";
    public static final String findOrderURL = "/{orderId}";
    public static final String updateOrderURL = "/{orderId}";
    public static final String deleteOrderURL = "/{orderId}";

    // auth api
    public static final String loginURL = "/users/login";
    public static final String logOutURL = "/users/logout";

    // toss payments api
    public static final String tossPaymentAuthURL = "/toss/payment/auth";
}
