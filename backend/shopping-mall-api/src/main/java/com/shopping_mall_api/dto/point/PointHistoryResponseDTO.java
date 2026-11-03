package com.shopping_mall_api.dto.point;

import com.shopping_mall_api.entity.point.PointHistory;
import com.shopping_mall_api.entity.point.PointHistoryType;

import java.time.LocalDateTime;

public record PointHistoryResponseDTO(
        Long pointHistoryId,
        PointHistoryType type,
        Long amount,
        Long orderId,
        String description,
        LocalDateTime createdAt
) {
    public static PointHistoryResponseDTO from(PointHistory pointHistory){
        throw new UnsupportedOperationException("TODO");
    }
}
