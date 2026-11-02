package com.shopping_mall_api.dto.cart.cartItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemCreateDTO {
    @NotNull(message = "productId must be necessary")
    private Long productId;

    @NotNull(message = "quantity must be necessary")
    @Min(value = 1, message = "product quantity must be at least 1")
    private Long quantity;
}