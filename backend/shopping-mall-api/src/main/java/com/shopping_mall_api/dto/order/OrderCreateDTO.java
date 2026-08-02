package com.shopping_mall_api.dto.order;

import com.shopping_mall_api.dto.order.orderItem.OrderItemCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {
    @NotNull(message = "userId must not be null")
    private Long userId;

    @Valid
    @NotEmpty(message = "orderItemCreateDTOList must have 1 orderItemCreateDTO at least")
    private List<OrderItemCreateDTO> orderItemCreateDTOList;
}
