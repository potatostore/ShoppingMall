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

    public CartItemResponseDTO(CartItem cartItem){
        CheckConfig.npeCheck(cartItem, "cartItem");

        this.cartItemId = cartItem.getCartItemId();
        this.productItemId = cartItem.getProduct().getProductId();
        this.quantity = cartItem.getQuantity();
    }
}