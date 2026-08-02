package com.shopping_mall_api.repository.order;

import com.shopping_mall_api.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
