package com.shopping_mall_api.Repository;

import com.shopping_mall_api.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
