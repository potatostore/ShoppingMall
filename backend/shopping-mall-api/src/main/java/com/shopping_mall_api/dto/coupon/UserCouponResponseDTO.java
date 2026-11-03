package com.shopping_mall_api.dto.coupon;

import com.shopping_mall_api.entity.coupon.UserCoupon;

import java.time.LocalDateTime;

public record UserCouponResponseDTO(
        Long userCouponId,
        CouponResponseDTO coupon,
        boolean used,
        LocalDateTime usedAt
) {
    public static UserCouponResponseDTO from(UserCoupon userCoupon){
        throw new UnsupportedOperationException("TODO");
    }
}
