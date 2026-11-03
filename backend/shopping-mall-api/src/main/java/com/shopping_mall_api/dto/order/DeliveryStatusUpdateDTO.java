package com.shopping_mall_api.dto.order;

import com.shopping_mall_api.entity.order.DeliveryStatus;

// [뼈대] 관리자 배송 상태 변경. SHIPPING으로 바꿀 때 trackingNumber 필수
public record DeliveryStatusUpdateDTO(
        DeliveryStatus deliveryStatus,
        String trackingNumber
) {
}
