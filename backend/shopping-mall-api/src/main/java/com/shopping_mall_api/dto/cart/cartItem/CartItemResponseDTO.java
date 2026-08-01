package com.shopping_mall_api.dto.cart.cartItem;

import com.shopping_mall_api.entity.cart.CartItem;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long productId;
    private Long curProductPrice;
    private Long quantity;
    private Long totalPrice;

    public CartItemResponseDTO(CartItem item){
        if(item == null){
            throw new IllegalArgumentException("Create CartItemResponseDTO : CartItem cannot be null");
        }
        this.cartItemId = item.getCartItemId();
        this.productId = item.getProductId();
        this.curProductPrice = (item.getCurProductPrice() != null && item.getCurProductPrice() >= 0) ? item.getCurProductPrice() : 0;
        this.quantity = (item.getQuantity() != null && item.getQuantity() >= 0) ? item.getQuantity() : 0;
        this.totalPrice = this.curProductPrice * this.quantity;
    }
}