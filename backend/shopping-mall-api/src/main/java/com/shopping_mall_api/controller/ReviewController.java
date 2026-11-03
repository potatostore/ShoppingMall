package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.review.RatingSummaryDTO;
import com.shopping_mall_api.dto.review.ReviewCreateDTO;
import com.shopping_mall_api.dto.review.ReviewResponseDTO;
import com.shopping_mall_api.dto.review.ReviewUpdateDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

// [뼈대] 구현 시: @RestController, @RequiredArgsConstructor, @CrossOrigin
//         SecurityConfig: 조회는 공개, 작성·수정·삭제는 로그인 필요
public class ReviewController {

    // POST /reviews
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> createReview(Long userId, ReviewCreateDTO reviewCreateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    // GET /products/{productId}/reviews?page=&size=  (공개)
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getProductReviews(Long productId, int page, int size){
        throw new UnsupportedOperationException("TODO");
    }

    // GET /products/{productId}/reviews/summary  (공개)
    public ResponseEntity<ApiResponse<RatingSummaryDTO>> getRatingSummary(Long productId){
        throw new UnsupportedOperationException("TODO");
    }

    // PATCH /reviews/{reviewId}
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> updateReview(Long userId, Long reviewId, ReviewUpdateDTO reviewUpdateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    // DELETE /reviews/{reviewId}
    public void deleteReview(Long userId, Long reviewId){
        throw new UnsupportedOperationException("TODO");
    }
}
