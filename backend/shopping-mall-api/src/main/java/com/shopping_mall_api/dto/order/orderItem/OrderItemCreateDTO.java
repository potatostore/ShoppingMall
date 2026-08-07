package com.shopping_mall_api.dto.order.orderItem;

import com.shopping_mall_api.global.config.CheckConfig;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemCreateDTO {
    @NotNull(message = "productId must not be null")
    private Long productId;

    @NotNull(message = "curOrderItemPrice must not be null")
    @Min(value = 0, message = "curOrderItemPrice must not be negative")
    private Long curOrderItemPrice;

    @NotNull(message = "quantity must not be null")
    @Min(value = 0, message = "quantity must not be negative")
    private Long quantity;

    @Builder
    public OrderItemCreateDTO(Long productId, Long curOrderItemPrice, Long quantity){
        CheckConfig.npeCheck(productId, "productId");
        CheckConfig.npeAndNegativeCheck(curOrderItemPrice, "curOrderItemPrice");
        CheckConfig.npeAndNegativeCheck(quantity, "quantity");

        this.productId = productId;
        this.curOrderItemPrice = curOrderItemPrice;
        this.quantity = quantity;
    }
}
