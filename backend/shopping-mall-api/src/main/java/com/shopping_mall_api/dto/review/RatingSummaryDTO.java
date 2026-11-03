package com.shopping_mall_api.dto.review;

// 상품 상세에 노출할 평균 별점·리뷰 수
public record RatingSummaryDTO(
        Long productId,
        double averageRating,
        long reviewCount
) {
}
