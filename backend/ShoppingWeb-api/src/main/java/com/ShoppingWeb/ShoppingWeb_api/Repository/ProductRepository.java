package com.ShoppingWeb.ShoppingWeb_api.Repository;

import com.ShoppingWeb.ShoppingWeb_api.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
