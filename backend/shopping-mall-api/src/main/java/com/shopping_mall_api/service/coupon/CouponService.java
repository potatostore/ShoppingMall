package com.shopping_mall_api.service.coupon;

import com.shopping_mall_api.dto.coupon.CouponCreateDTO;
import com.shopping_mall_api.dto.coupon.CouponResponseDTO;
import com.shopping_mall_api.dto.coupon.UserCouponResponseDTO;

import java.util.List;

// [뼈대] 쿠폰 발급·조회·주문 적용
// 구현 시: @Service, @RequiredArgsConstructor, @Transactional(readOnly = true) + 쓰기 메서드 @Transactional
public class CouponService {

    // 관리자
    public CouponResponseDTO createCoupon(CouponCreateDTO couponCreateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    public List<CouponResponseDTO> getAvailableCoupons(){
        throw new UnsupportedOperationException("TODO");
    }

    // 선착순 발급: 기간 확인 → 중복 발급 확인 → 수량 조건부 UPDATE(0이면 소진 400) → UserCoupon 저장
    // 동시 요청은 unique 제약(user_id, coupon_id)과 조건부 UPDATE로 막음. 동시성 테스트 필수 (ProductStockConcurrencyTest 참고)
    public UserCouponResponseDTO issueCoupon(Long userId, Long couponId){
        throw new UnsupportedOperationException("TODO");
    }

    public List<UserCouponResponseDTO> getMyCoupons(Long userId){
        throw new UnsupportedOperationException("TODO");
    }

    // 주문 생성 시 호출(OrderService.createOrder의 "쿠폰 및 포인트 차감 로직" TODO): 본인·미사용·기간·최소 금액 확인 후 할인액 반환, 사용 처리
    public long applyCoupon(Long userId, Long userCouponId, Long orderId, long orderAmount){
        throw new UnsupportedOperationException("TODO");
    }

    // 주문 취소·결제 실패·만료 시 호출 (restoreStock과 같은 지점)
    public void restoreCouponOfOrder(Long orderId){
        throw new UnsupportedOperationException("TODO");
    }
}
