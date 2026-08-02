package com.shopping_mall_api.dto.cart.cartItem;

import com.shopping_mall_api.entity.cart.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long productId;
    private Long curProductPrice;
    private Long quantity;
    private Long totalPrice;

    public CartItemResponseDTO(CartItem cartItem){
        Objects.requireNonNull(cartItem, "cartItem must not be null");

        this.cartItemId = cartItem.getCartItemId();
        this.productId = cartItem.getProductId();
        this.curProductPrice = cartItem.getCurProductPrice();
        this.quantity = cartItem.getQuantity();
        this.totalPrice = cartItem.getTotalProductPrice();
    }
}