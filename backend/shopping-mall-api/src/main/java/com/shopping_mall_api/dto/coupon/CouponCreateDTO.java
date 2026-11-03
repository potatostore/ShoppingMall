package com.shopping_mall_api.dto.coupon;

import com.shopping_mall_api.entity.coupon.DiscountType;

import java.time.LocalDateTime;

// [뼈대] 구현 시: @NotNull, @Min, RATE면 discountValue 1~100, startAt < endAt 검증
public record CouponCreateDTO(
        String name,
        DiscountType discountType,
        Long discountValue,
        Long minOrderAmount,
        Long maxDiscountAmount,
        Long totalQuantity,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
