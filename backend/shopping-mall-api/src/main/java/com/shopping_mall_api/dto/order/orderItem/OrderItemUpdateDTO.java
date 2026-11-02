package com.shopping_mall_api.dto.order.orderItem;

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
public class OrderItemUpdateDTO {
    @NotNull(message = "productId must not be null")
    private Long productId;

    @NotNull(message = "curOrderItemPrice must not be null")
    @Min(value = 0, message = "curOrderItemPrice must be at least 0")
    private Long curOrderItemPrice;

    @NotNull(message = "quantity must not be null")
    @Min(value = 1, message = "quantity must be at least 1")
    private Long quantity;
}
