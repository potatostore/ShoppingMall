package com.shopping_mall_api.Repository;

import com.shopping_mall_api.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
