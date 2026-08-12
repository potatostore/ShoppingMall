package com.shopping_mall_api.entity.order;

import com.shopping_mall_api.dto.order.orderItem.OrderItemCreateDTO;
import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.entity.cart.Cart;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = TableNames.orderTableName)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderItem> orderItemList;

    @NotNull
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @NotNull(message = "totalOrderPrice must not be null")
    @Column(nullable = false)
    @Min(value = 0, message = "totalOrderPrice must not be negative")
    private Long totalOrderPrice;

    @Builder
    public Order(User user, List<OrderItemCreateDTO> orderItemCreateDTOList, OrderStatus orderStatus) {
        CheckConfig.npeCheck(user, "user");
        CheckConfig.npeAndEmptyCheck(orderItemCreateDTOList, "orderItemCreateDTOList");
        CheckConfig.npeCheck(orderStatus, "orderStatus");

        this.orderItemList = new ArrayList<>();
        orderItemCreateDTOList.forEach(this::addOrderItem);
        this.user = user;
        this.orderStatus = orderStatus;
    }

    public void addOrderItem(OrderItemCreateDTO orderItemCreateDTO){
        CheckConfig.npeCheck(orderItemCreateDTO, "orderItemCreateDTO");

        OrderItem addOrderItem = OrderItem.builder()
                                    .productId(orderItemCreateDTO.getProductId())
                                    .quantity(orderItemCreateDTO.getQuantity())
                                    .curOrderItemPrice(orderItemCreateDTO.getCurOrderItemPrice())
                                    .build();

        addOrderItem.assignOrder(this);

        orderItemList.add(addOrderItem);

        updateTotalOrderPrice();
    }

    public void updateTotalOrderPrice(){
        CheckConfig.npeAndEmptyCheck(this.orderItemList, "orderItemList");

        this.totalOrderPrice = orderItemList.stream()
                .mapToLong(OrderItem::getTotalOrderItemPrice).sum();
    }

    public void completePayment(){
        this.orderStatus = OrderStatus.PAID;
    }

    public void failPayment(){
        this.orderStatus = OrderStatus.FAILED;
        //주문 재고 복구
        //쿠폰 및 포인트 복구(미사용 처리)
        //실패 사유(로그 저장)
    }
}
