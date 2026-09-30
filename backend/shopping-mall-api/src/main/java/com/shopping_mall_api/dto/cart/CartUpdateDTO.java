package com.shopping_mall_api.dto.cart;

import com.shopping_mall_api.dto.cart.cartItem.CartItemUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartUpdateDTO {
    private List<CartItemUpdateDTO> cartItemUpdateDTOList;
}
