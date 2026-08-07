package com.shopping_mall_api.dto.order.orderItem;

import com.shopping_mall_api.entity.order.OrderItem;
import com.shopping_mall_api.global.config.CheckConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemResponseDTO {
    private Long productId;
    private Long curOrderItemPrice;
    private Long quantity;
    private Long totalOrderItemPrice;

    @Builder
    public OrderItemResponseDTO(OrderItem orderItem){
        CheckConfig.npeCheck(orderItem, "orderItem");

        this.productId = orderItem.getProductId();
        this.curOrderItemPrice = orderItem.getCurOrderItemPrice();
        this.quantity = orderItem.getQuantity();
        this.totalOrderItemPrice = orderItem.getTotalOrderItemPrice();
    }
}
