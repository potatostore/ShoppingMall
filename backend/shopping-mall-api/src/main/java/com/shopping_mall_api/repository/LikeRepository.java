package com.shopping_mall_api.repository;

import com.shopping_mall_api.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Integer> {
}
