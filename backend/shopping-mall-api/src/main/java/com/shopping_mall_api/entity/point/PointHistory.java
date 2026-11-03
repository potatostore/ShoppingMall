package com.shopping_mall_api.entity.point;

import com.shopping_mall_api.entity.user.User;

// [뼈대] 포인트 변동 이력 (적립 +, 사용 -). 잔액은 이력 합계 또는 User의 잔액 컬럼으로 관리
// 구현 시: @Entity, @Table(name = "point_history"), BaseEntity 상속(createdAt = 발생 시각)
//         잔액 컬럼 방식을 고르면 User에 pointBalance 추가 + 차감은 조건부 UPDATE(잔액 >= 사용액)로 동시 사용 시 음수 방지
public class PointHistory {
    private Long pointHistoryId;
    private User user;
    private PointHistoryType type;
    private Long amount;        // 적립·복구는 양수, 사용·적립 취소는 음수
    private Long orderId;
    private String description;
}
