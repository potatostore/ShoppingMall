package com.shopping_mall_api.repository.user;

import com.shopping_mall_api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findBySignInId(String signInId);
}
