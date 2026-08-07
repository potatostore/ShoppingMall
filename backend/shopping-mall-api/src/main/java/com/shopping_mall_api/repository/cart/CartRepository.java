package com.shopping_mall_api.repository.cart;

import com.shopping_mall_api.entity.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    public Optional<Cart> findByUserId(Long userId);
    public void deleteByUserId(Long userId);
}
