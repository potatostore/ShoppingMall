package com.shopping_mall_api.entity.order;

import com.shopping_mall_api.dto.order.orderItem.OrderItemCreateDTO;
import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = TableNames.orderItemTableName)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull(message = "productId must not be null")
    @Column(nullable = false)
    private Long productId;

    @NotNull(message = "curOrderItemPrice must not be null")
    @Column(nullable = false)
    @Min(value = 0, message = "curOrderItemPrice must not be negative")
    private Long curOrderItemPrice;

    @NotNull(message = "quantity must not be null")
    @Column(nullable = false)
    @Min(value = 0, message = "quantity must not be negative")
    private Long quantity;

    @Column(nullable = false)
    @Min(value = 0, message = "totalOrderItemPrice must not be negative")
    private Long totalOrderItemPrice;

    @Builder
    public OrderItem(Long productId, Long curOrderItemPrice, Long quantity){
        CheckConfig.npeCheck(productId, "productId");
        CheckConfig.npeAndNegativeCheck(curOrderItemPrice, "curOrderItemPrice");
        CheckConfig.npeAndNegativeCheck(quantity, "quantity");

        this.curOrderItemPrice = curOrderItemPrice;
        this.quantity = quantity;
        this.totalOrderItemPrice = this.quantity * this.curOrderItemPrice;
    }

    public OrderItem(OrderItemCreateDTO orderItemCreateDTO){
        this(orderItemCreateDTO.getProductId(),
             orderItemCreateDTO.getCurOrderItemPrice(),
             orderItemCreateDTO.getQuantity());
    }
}
