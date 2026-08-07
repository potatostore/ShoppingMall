package com.shopping_mall_api.dto.cart;

import com.shopping_mall_api.dto.cart.cartItem.CartItemResponseDTO;
import com.shopping_mall_api.entity.cart.Cart;
import com.shopping_mall_api.global.config.CheckConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {
    private Long userId;
    private List<CartItemResponseDTO> cartItemList;
    private Long totalCartPrice;

    public CartResponseDTO(Cart cart){
        CheckConfig.npeCheck(cart, "cart");

        this.userId = cart.getUserId();
        this.cartItemList = cart.getCartItemList().stream()
                .map(CartItemResponseDTO::new).toList();
        this.totalCartPrice = cart.getTotalCartPrice();
    }
}