package com.shopping_mall_api.dto.cart.cartItem;

import com.shopping_mall_api.entity.cart.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long productId;
    private Integer curProductPrice;
    private Integer quantity;
    private Integer totalPrice;

    public CartItemResponseDTO(CartItem item){
        this.cartItemId = item.getCartItemId();
        this.productId = item.getProductId();
        this.curProductPrice = item.getCurProductPrice();
        this.quantity = item.getQuantity();
        this.totalPrice = item.getTotalProductPrice();
    }
}