package com.shopping_mall_api.dto.coupon;

import com.shopping_mall_api.entity.coupon.Coupon;
import com.shopping_mall_api.entity.coupon.DiscountType;

import java.time.LocalDateTime;

public record CouponResponseDTO(
        Long couponId,
        String name,
        DiscountType discountType,
        Long discountValue,
        Long minOrderAmount,
        Long maxDiscountAmount,
        Long remainingQuantity,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    public static CouponResponseDTO from(Coupon coupon){
        throw new UnsupportedOperationException("TODO");
    }
}
