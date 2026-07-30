package com.shopping_mall_api.repository;

import com.shopping_mall_api.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
