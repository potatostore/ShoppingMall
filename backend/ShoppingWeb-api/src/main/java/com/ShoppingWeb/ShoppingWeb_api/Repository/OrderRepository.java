package com.ShoppingWeb.ShoppingWeb_api.Repository;

import com.ShoppingWeb.ShoppingWeb_api.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
