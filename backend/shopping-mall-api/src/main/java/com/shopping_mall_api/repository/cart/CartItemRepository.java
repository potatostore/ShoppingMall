package com.shopping_mall_api.repository.cart;

import com.shopping_mall_api.entity.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
