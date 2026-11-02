package com.shopping_mall_api.dto.order.orderItem;

import com.shopping_mall_api.global.config.CheckConfig;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 상품 가격은 클라이언트가 보내지 않음: 서버가 Product에서 조회해 OrderItem.curOrderItemPrice에 기록
@Getter
@NoArgsConstructor
public class OrderItemCreateDTO {
    @NotNull(message = "productId must not be null")
    private Long productId;

    @NotNull(message = "quantity must not be null")
    @Min(value = 1, message = "quantity must be at least 1")
    private Long quantity;

    @Builder
    public OrderItemCreateDTO(Long productId, Long quantity){
        CheckConfig.npeCheck(productId, "productId");
        CheckConfig.npeAndNegativeCheck(quantity, "quantity");

        this.productId = productId;
        this.quantity = quantity;
    }
}
