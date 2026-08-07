package com.shopping_mall_api.entity.order;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderItem> orderItemList;

    @NotNull(message = "totalOrderPrice must not be null")
    @Column(nullable = false)
    @Min(value = 0, message = "totalOrderPrice must not be negative")
    private Long totalOrderPrice;

    @Builder
    public Order(User user, Cart cart) {
        CheckConfig.npeCheck(user, "user");
        CheckConfig.npeCheck(cart, "cart");

        this.orderItemList = new ArrayList<>();
        this.user = user;
        this.cart = cart;
        this.totalOrderPrice = 0L;
    }

    public void addOrderItem(OrderItem orderItem){
        CheckConfig.npeCheck(orderItem, "orderItem");

        orderItemList.add(orderItem);

        updateTotalOrderPrice();
    }

    public void updateTotalOrderPrice(){
        CheckConfig.npeAndEmptyCheck(this.orderItemList, "orderItemList");

        this.totalOrderPrice = orderItemList.stream()
                .mapToLong(OrderItem::getTotalOrderItemPrice).sum();
    }
}
