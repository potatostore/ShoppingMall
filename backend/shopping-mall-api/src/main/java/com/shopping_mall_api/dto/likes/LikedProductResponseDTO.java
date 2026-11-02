package com.shopping_mall_api.dto.likes;

import com.shopping_mall_api.entity.likes.Likes;

import java.time.LocalDateTime;

public record LikedProductResponseDTO(Long productId, String name, Long price, Long stock, LocalDateTime likedAt) {
    public LikedProductResponseDTO(Likes likes){
        this(likes.getProduct().getProductId(),
             likes.getProduct().getName(),
             likes.getProduct().getPrice(),
             likes.getProduct().getStock(),
             likes.getCreatedAt());
    }
}
