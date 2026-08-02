package com.shopping_mall_api.dto.order;

import com.shopping_mall_api.dto.order.orderItem.OrderItemResponseDTO;
import com.shopping_mall_api.entity.order.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderResponseDTO {
    private Long orderId;
    private List<OrderItemResponseDTO> orderItemResponseDTOList;
    private Long totalOrderPrice;

    @Builder
    public OrderResponseDTO(Order order){
        this.orderId = order.getOrderId();
        this.orderItemResponseDTOList = (order.getOrderItemList() != null)
                ? order.getOrderItemList().stream()
                .map(OrderItemResponseDTO::new)
                .toList()
                : List.of();
        this.totalOrderPrice = order.getTotalOrderPrice();
    }
}
