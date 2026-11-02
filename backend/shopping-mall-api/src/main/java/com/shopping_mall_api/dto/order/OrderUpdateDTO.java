package com.shopping_mall_api.dto.order;

import com.shopping_mall_api.dto.order.orderItem.OrderItemUpdateDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderUpdateDTO {
    @Valid
    private List<OrderItemUpdateDTO> orderItemResponseDTOList;
}
