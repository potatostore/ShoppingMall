package com.shopping_mall_api.repository.order;

import com.shopping_mall_api.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findByUser_UserId(Long userId);
    public Optional<Order> findByOrderUid(Long orderUid);
}
