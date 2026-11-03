package com.shopping_mall_api.service.order;

import com.shopping_mall_api.dto.order.DeliveryStatusUpdateDTO;
import com.shopping_mall_api.dto.order.OrderResponseDTO;

// [뼈대] 배송 상태 변경 · 구매확정
// 구현 시: @Service, @RequiredArgsConstructor, 쓰기 메서드 @Transactional, 주문 행 잠금(findByIdForUpdate) 사용
public class DeliveryService {

    // 관리자: 다음 단계로만 이동 가능 (READY → PREPARING → SHIPPING → DELIVERED), 건너뛰기·되돌리기는 400
    // PAID가 아닌 주문(PENDING, CANCELED 등)은 400
    public OrderResponseDTO updateDeliveryStatus(Long orderId, DeliveryStatusUpdateDTO deliveryStatusUpdateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    // 사용자: DELIVERED 주문만 구매확정 → CONFIRMED. 이후 포인트 적립(PointService.earnOnPurchaseConfirmed), 리뷰 작성 가능
    public OrderResponseDTO confirmPurchase(Long userId, Long orderId){
        throw new UnsupportedOperationException("TODO");
    }

    // 배송 완료 후 일정 기간(예: 7일) 지나면 자동 구매확정 (OrderExpirationScheduler와 같은 폴링 방식)
    public int autoConfirmDeliveredOrders(){
        throw new UnsupportedOperationException("TODO");
    }
}
