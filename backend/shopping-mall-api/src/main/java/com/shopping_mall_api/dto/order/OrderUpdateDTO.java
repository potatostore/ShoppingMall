package com.shopping_mall_api.dto.order;

import com.shopping_mall_api.dto.order.orderItem.OrderItemUpdateDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderUpdateDTO {
    private List<OrderItemUpdateDTO> orderItemResponseDTOList;
}
