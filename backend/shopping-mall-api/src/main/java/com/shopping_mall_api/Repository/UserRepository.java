package com.shopping_mall_api.Repository;

import com.shopping_mall_api.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
