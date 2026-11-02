package com.shopping_mall_api.entity.order;

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

    // 주문 시점 상품명 스냅샷 (상품명이 바뀌거나 상품이 삭제돼도 주문 내역은 그대로 보이도록). 이 컬럼 추가 전 주문은 null
    @Column(length = 255)
    private String productName;

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

    public OrderItem(Long productId, Long curOrderItemPrice, Long quantity){
        this(productId, null, curOrderItemPrice, quantity);
    }

    @Builder
    public OrderItem(Long productId, String productName, Long curOrderItemPrice, Long quantity){
        CheckConfig.npeCheck(productId, "productId");
        CheckConfig.npeAndNegativeCheck(curOrderItemPrice, "curOrderItemPrice");
        CheckConfig.npeAndNegativeCheck(quantity, "quantity");

        this.productId = productId;
        this.productName = productName;
        this.curOrderItemPrice = curOrderItemPrice;
        this.quantity = quantity;
        this.totalOrderItemPrice = this.quantity * this.curOrderItemPrice;
    }

    public void assignOrder(Order order){
        CheckConfig.npeCheck(order, "order");

        this.order = order;
    }

    public void updateQuantity(Long quantity){
        CheckConfig.npeAndNegativeCheck(quantity, "quantity");

        this.quantity = quantity;

        updateTotalItemPrice();
    }

    public void updateTotalItemPrice(){
        this.totalOrderItemPrice = this.quantity * this.curOrderItemPrice;
    }
}
