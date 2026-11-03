package com.shopping_mall_api.entity.coupon;

import java.time.LocalDateTime;

// [뼈대] 쿠폰 정의 (관리자가 만듦). 실제 사용자에게 발급된 쿠폰은 UserCoupon
// 구현 시: @Entity, @Table(name = "coupon"), BaseEntity 상속, discountType @Enumerated(STRING) + @JdbcTypeCode(VARCHAR)
public class Coupon {
    private Long couponId;
    private String name;
    private DiscountType discountType;
    private Long discountValue;      // FIXED면 원, RATE면 %
    private Long minOrderAmount;     // 최소 주문 금액
    private Long maxDiscountAmount;  // RATE 쿠폰의 최대 할인액 (null이면 상한 없음)
    private Long totalQuantity;      // 선착순 발급 수량 (null이면 무제한)
    private Long issuedQuantity;     // 발급된 수량
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    // 주문 금액 기준 할인액 계산 (최소 주문 금액 미달이면 400, 할인액은 주문 금액을 넘지 않음)
    public long calculateDiscount(long orderAmount){
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isAvailableAt(LocalDateTime now){
        throw new UnsupportedOperationException("TODO");
    }
}
