package com.shopping_mall_api.entity.point;

// [뼈대] EARN: 구매확정 적립, USE: 주문 시 사용, RESTORE: 주문 취소로 사용분 복구, CANCEL_EARN: 적립 취소(구매확정 후 환불 등)
public enum PointHistoryType {
    EARN,
    USE,
    RESTORE,
    CANCEL_EARN
}
