package com.shopping_mall_api.repository.user;

import com.shopping_mall_api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);
    public boolean existsByEmail(String email);
    public List<User> findByNameAndPhoneNumber(String name, String phoneNumber);
}
