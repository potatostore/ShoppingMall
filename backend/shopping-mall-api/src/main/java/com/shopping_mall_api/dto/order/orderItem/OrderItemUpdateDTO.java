package com.shopping_mall_api.dto.order.orderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주문 수정은 수량만 가능. 가격은 주문 시점 가격(OrderItem.curOrderItemPrice)을 유지
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemUpdateDTO {
    @NotNull(message = "productId must not be null")
    private Long productId;

    @NotNull(message = "quantity must not be null")
    @Min(value = 1, message = "quantity must be at least 1")
    private Long quantity;
}
