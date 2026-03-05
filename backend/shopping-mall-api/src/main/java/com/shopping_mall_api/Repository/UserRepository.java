package com.shopping_mall_api.Repository;

import com.shopping_mall_api.DataTransferObject.SignUpData;
import com.shopping_mall_api.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
}
