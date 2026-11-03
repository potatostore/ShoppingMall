package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.coupon.CouponCreateDTO;
import com.shopping_mall_api.dto.coupon.CouponResponseDTO;
import com.shopping_mall_api.dto.coupon.UserCouponResponseDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

// [뼈대] 구현 시: @RestController, @RequiredArgsConstructor, @CrossOrigin, ApiURLNames·SecurityConfig 등록
public class CouponController {

    // POST /coupons  (ADMIN)
    public ResponseEntity<ApiResponse<CouponResponseDTO>> createCoupon(CouponCreateDTO couponCreateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    // GET /coupons  (발급 가능한 쿠폰 목록, 공개 또는 로그인 필요)
    public ResponseEntity<ApiResponse<List<CouponResponseDTO>>> getAvailableCoupons(){
        throw new UnsupportedOperationException("TODO");
    }

    // POST /coupons/{couponId}/issue  (로그인 필요)
    public ResponseEntity<ApiResponse<UserCouponResponseDTO>> issueCoupon(Long userId, Long couponId){
        throw new UnsupportedOperationException("TODO");
    }

    // GET /users/me/coupons
    public ResponseEntity<ApiResponse<List<UserCouponResponseDTO>>> getMyCoupons(Long userId){
        throw new UnsupportedOperationException("TODO");
    }
}
