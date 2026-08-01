package com.shopping_mall_api.dto.cart.cartItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemCreateDTO {
    @NotNull(message = "productId는 필수입니다.")
    private Long productId;

    @NotNull(message = "quantity는 필수입니다.")
    @Min(value = 1, message = "quantity는 최소 1개 이상이여야 합니다.")
    private Long quantity;
}