package com.shopping_mall_api.entity.order;

// [뼈대] 배송 상태. 결제 상태(OrderStatus)와 분리 → PAID 주문에만 의미가 있음
// 흐름: READY(결제 완료, 배송 전) → PREPARING → SHIPPING → DELIVERED → CONFIRMED(구매확정)
// 구현 시: Order에 @Enumerated(STRING) @JdbcTypeCode(SqlTypes.VARCHAR) deliveryStatus, trackingNumber, deliveredAt, confirmedAt 추가
//         주문 취소(OrderService.cancelOrder)는 PREPARING 전(READY)까지만 허용하도록 연결
public enum DeliveryStatus {
    READY,
    PREPARING,
    SHIPPING,
    DELIVERED,
    CONFIRMED
}
