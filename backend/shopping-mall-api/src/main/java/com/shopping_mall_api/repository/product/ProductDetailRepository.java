package com.shopping_mall_api.repository.product;

import com.shopping_mall_api.entity.product.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
}
