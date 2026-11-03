package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.order.DeliveryStatusUpdateDTO;
import com.shopping_mall_api.dto.order.OrderResponseDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import org.springframework.http.ResponseEntity;

// [뼈대] 구현 시: @RestController, @RequiredArgsConstructor, @CrossOrigin
//         SecurityConfig: 배송 상태 변경은 ADMIN, 구매확정은 로그인 필요
//         주문 생성(OrderCreateDTO)에 addressId(또는 배송지 직접 입력)와 deliveryRequest 추가 → Order.shippingInfo 스냅샷 저장
public class DeliveryController {

    // PATCH /orders/{orderId}/delivery  (ADMIN)
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateDeliveryStatus(Long orderId, DeliveryStatusUpdateDTO deliveryStatusUpdateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    // POST /orders/{orderId}/confirm  (본인 주문)
    public ResponseEntity<ApiResponse<OrderResponseDTO>> confirmPurchase(Long userId, Long orderId){
        throw new UnsupportedOperationException("TODO");
    }
}
