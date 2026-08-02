package com.shopping_mall_api.dto.order.orderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(curOrderItemPrice, "curOrderItemPrice must not be null");
        Objects.requireNonNull(quantity, "quantity must not be null");

        if(curOrderItemPrice < 0){
            throw new IllegalArgumentException("curOrderItemPrice must not be negative");
        }
        if(quantity < 0){
            throw new IllegalArgumentException("quantity must not be negative");
        }

        this.productId = productId;
        this.curOrderItemPrice = curOrderItemPrice;
        this.quantity = quantity;
    }
}
