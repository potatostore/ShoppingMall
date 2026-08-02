package com.shopping_mall_api.repository.order;

import com.shopping_mall_api.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
