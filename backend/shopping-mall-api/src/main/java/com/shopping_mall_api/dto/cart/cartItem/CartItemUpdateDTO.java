package com.shopping_mall_api.dto.cart.cartItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUpdateDTO {
    @NotNull(message = "productId must not be null")
    private Long productId;

    @NotNull
    @Min(value = 1, message = "product quantity must be at least 1")
    private Long quantity;
}