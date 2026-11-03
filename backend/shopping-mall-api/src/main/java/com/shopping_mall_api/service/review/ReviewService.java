package com.shopping_mall_api.service.review;

import com.shopping_mall_api.dto.review.RatingSummaryDTO;
import com.shopping_mall_api.dto.review.ReviewCreateDTO;
import com.shopping_mall_api.dto.review.ReviewResponseDTO;
import com.shopping_mall_api.dto.review.ReviewUpdateDTO;

import java.util.List;

// [뼈대] 리뷰 작성·조회·수정·삭제
// 구현 시: @Service, @RequiredArgsConstructor, @Transactional(readOnly = true) + 쓰기 메서드 @Transactional
public class ReviewService {

    // 본인 주문 항목인지, 주문이 CONFIRMED인지, 이미 작성했는지 확인 (아니면 403/400)
    public ReviewResponseDTO createReview(Long userId, ReviewCreateDTO reviewCreateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    public List<ReviewResponseDTO> getProductReviews(Long productId, int page, int size){
        throw new UnsupportedOperationException("TODO");
    }

    public RatingSummaryDTO getRatingSummary(Long productId){
        throw new UnsupportedOperationException("TODO");
    }

    public ReviewResponseDTO updateReview(Long userId, Long reviewId, ReviewUpdateDTO reviewUpdateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    public void deleteReview(Long userId, Long reviewId){
        throw new UnsupportedOperationException("TODO");
    }
}
