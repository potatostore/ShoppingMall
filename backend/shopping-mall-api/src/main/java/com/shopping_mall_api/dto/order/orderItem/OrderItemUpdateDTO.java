package com.shopping_mall_api.dto.order.orderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemUpdateDTO {
    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 0)
    private Long curOrderItemPrice;

    @NotNull
    @Min(value = 1)
    private Long quantity;
}
