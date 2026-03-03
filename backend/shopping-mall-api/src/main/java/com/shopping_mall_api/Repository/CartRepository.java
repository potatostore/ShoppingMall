package com.shopping_mall_api.Repository;

import com.shopping_mall_api.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
}
