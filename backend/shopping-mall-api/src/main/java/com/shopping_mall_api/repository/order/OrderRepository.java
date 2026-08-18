package com.shopping_mall_api.repository.order;

import com.shopping_mall_api.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findByUserId(Long userId);
}
