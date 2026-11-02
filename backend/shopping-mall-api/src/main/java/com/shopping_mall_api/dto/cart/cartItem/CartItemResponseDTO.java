package com.shopping_mall_api.dto.cart.cartItem;

import com.shopping_mall_api.entity.cart.CartItem;
import com.shopping_mall_api.global.config.CheckConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long productItemId;
    private Long quantity;
    // 장바구니 화면이 상품 전체 목록을 따로 받지 않아도 되도록 이름·현재 가격을 함께 내려줌
    private String productName;
    private Long productPrice;

    public CartItemResponseDTO(CartItem cartItem){
        CheckConfig.npeCheck(cartItem, "cartItem");

        this.cartItemId = cartItem.getCartItemId();
        this.productItemId = cartItem.getProduct().getProductId();
        this.quantity = cartItem.getQuantity();
        this.productName = cartItem.getProduct().getName();
        this.productPrice = cartItem.getProduct().getPrice();
    }

}