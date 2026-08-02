package com.shopping_mall_api.repository.cart;

import com.shopping_mall_api.entity.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
