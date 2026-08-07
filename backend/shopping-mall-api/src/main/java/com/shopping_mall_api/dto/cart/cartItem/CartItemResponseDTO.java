package com.shopping_mall_api.dto.cart.cartItem;

import com.shopping_mall_api.entity.cart.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long quantity;

    public CartItemResponseDTO(CartItem cartItem){
        Objects.requireNonNull(cartItem, "cartItem must not be null");

        this.cartItemId = cartItem.getCartItemId();
        this.quantity = cartItem.getQuantity();
    }
}