package com.shopping_mall_api.global.constant;

public class ApiURLNames {
    // users api
    public static final String userURL = "/users";
    public static final String createUserURL = "/signup";
    public static final String findUsersURL = "";
    public static final String findUserURL = "/me";
    public static final String updateUserURL = "/me";
    public static final String deleteUserURL = "/me";
    public static final String findIdURL = "/find-id";

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
    public static final String cancelOrderURL = "/{orderId}/cancel";

    // auth api
    public static final String loginURL = "/users/login";
    public static final String logOutURL = "/users/logout";
    public static final String reissueURL = "/auth/reissue";

    // likes api (컨트롤러에 공통 prefix 없이 전체 경로)
    public static final String productLikesURL = "/products/{productId}/likes";
    public static final String myProductLikeURL = "/products/{productId}/likes/me";
    public static final String myLikesURL = "/users/me/likes";

    // toss payments api
    public static final String tossPaymentAuthURL = "/toss/payment/auth";
}
