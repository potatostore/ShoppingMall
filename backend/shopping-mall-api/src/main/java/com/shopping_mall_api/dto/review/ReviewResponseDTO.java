package com.shopping_mall_api.dto.review;

import com.shopping_mall_api.entity.review.Review;

import java.time.LocalDateTime;

// 작성자 이름은 마스킹해서 노출 (예: 김*준)
public record ReviewResponseDTO(
        Long reviewId,
        Long productId,
        String writerName,
        Integer rating,
        String content,
        LocalDateTime createdAt
) {
    public static ReviewResponseDTO from(Review review){
        throw new UnsupportedOperationException("TODO");
    }
}
