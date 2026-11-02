package com.shopping_mall_api.dto.likes;

// 찜 여부와 해당 상품의 전체 찜 수
public record LikeStatusResponseDTO(Long productId, boolean liked, long likeCount) {
}
