package com.ShoppingWeb.ShoppingWeb_api.Repository;

import com.ShoppingWeb.ShoppingWeb_api.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
