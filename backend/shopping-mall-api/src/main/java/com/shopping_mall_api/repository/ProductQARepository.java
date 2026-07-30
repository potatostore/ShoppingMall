package com.shopping_mall_api.repository;

import com.shopping_mall_api.entity.product.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductQARepository extends JpaRepository<ProductDetail, Integer> {
}
