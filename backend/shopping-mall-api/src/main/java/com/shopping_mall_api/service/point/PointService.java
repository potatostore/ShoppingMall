package com.shopping_mall_api.service.point;

import com.shopping_mall_api.dto.point.PointBalanceResponseDTO;
import com.shopping_mall_api.dto.point.PointHistoryResponseDTO;

import java.util.List;

// [뼈대] 포인트 적립·사용·복구
// 구현 시: @Service, @RequiredArgsConstructor, 쓰기 메서드 @Transactional
public class PointService {

    public PointBalanceResponseDTO getBalance(Long userId){
        throw new UnsupportedOperationException("TODO");
    }

    public List<PointHistoryResponseDTO> getHistories(Long userId, int page, int size){
        throw new UnsupportedOperationException("TODO");
    }

    // 구매확정 시 호출(DeliveryService.confirmPurchase): 결제 금액의 일정 비율(예: 1%) 적립, 같은 주문 중복 적립 방지
    public void earnOnPurchaseConfirmed(Long userId, Long orderId, long paidAmount){
        throw new UnsupportedOperationException("TODO");
    }

    // 주문 생성 시 호출: 잔액 부족이면 400. 토스 결제 금액 = 상품 합계 - 쿠폰 할인 - 사용 포인트 (Order.totalOrderPrice 계산과 함께 변경)
    public void usePoint(Long userId, Long orderId, long amount){
        throw new UnsupportedOperationException("TODO");
    }

    // 주문 취소·결제 실패·만료 시 호출: 해당 주문에서 사용한 포인트 복구
    public void restorePointOfOrder(Long orderId){
        throw new UnsupportedOperationException("TODO");
    }
}
