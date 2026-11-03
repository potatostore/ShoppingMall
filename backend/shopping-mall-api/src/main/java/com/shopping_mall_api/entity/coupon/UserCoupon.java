package com.shopping_mall_api.entity.coupon;

import com.shopping_mall_api.entity.user.User;

import java.time.LocalDateTime;

// [뼈대] 사용자에게 발급된 쿠폰
// 구현 시: @Entity, @Table(name = "user_coupon", uniqueConstraints = (user_id, coupon_id)) → 같은 쿠폰 중복 발급 방지
public class UserCoupon {
    private Long userCouponId;
    private User user;
    private Coupon coupon;
    private boolean used;
    private LocalDateTime usedAt;
    private Long orderId;   // 사용한 주문

    public void use(Long orderId){
        throw new UnsupportedOperationException("TODO");
    }

    // 주문 취소·결제 실패·만료 시 미사용으로 되돌림
    public void restore(){
        throw new UnsupportedOperationException("TODO");
    }
}
