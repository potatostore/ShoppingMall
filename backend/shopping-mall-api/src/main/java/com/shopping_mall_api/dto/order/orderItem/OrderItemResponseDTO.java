package com.shopping_mall_api.dto.order.orderItem;

import com.shopping_mall_api.entity.order.OrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
public class OrderItemResponseDTO {
    private Long productId;
    private Long curOrderItemPrice;
    private Long quantity;
    private Long totalOrderItemPrice;

    @Builder
    public OrderItemResponseDTO(OrderItem orderItem){
        Objects.requireNonNull(orderItem, "orderItem must not be null");

        this.productId = orderItem.getProductId();
        this.curOrderItemPrice = orderItem.getCurOrderItemPrice();
        this.quantity = orderItem.getQuantity();
        this.totalOrderItemPrice = orderItem.getTotalOrderItemPrice();
    }
}
