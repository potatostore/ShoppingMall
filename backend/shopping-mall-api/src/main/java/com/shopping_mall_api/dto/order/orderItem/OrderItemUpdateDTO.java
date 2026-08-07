package com.shopping_mall_api.dto.order.orderItem;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemUpdateDTO {
    private Long curOrderItemPrice;
    private Long quantity;
}
