package com.shopping_mall_api.service.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// 결제창에서 이탈한 PENDING 주문이 재고를 계속 붙잡지 않도록 주기적으로 정리
// 주문마다 OrderService.expireIfStale을 따로 호출 → 주문 하나가 실패해도 나머지는 진행, 행 잠금도 짧게 유지
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpirationScheduler {
    private static final int BATCH_SIZE = 100;

    private final OrderService orderService;

    // 토스 결제창 인증 유효 시간(약 10분)보다 넉넉하게 기본 30분
    @Value("${order.pending-expiration-minutes:30}")
    private long pendingExpirationMinutes;

    @Scheduled(fixedDelayString = "${order.pending-expiration.check-interval-ms:60000}",
               initialDelayString = "${order.pending-expiration.initial-delay-ms:60000}")
    public void expireStalePendingOrders(){
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(pendingExpirationMinutes);
        List<Long> orderIds = orderService.findStalePendingOrderIds(threshold, BATCH_SIZE);

        for(Long orderId : orderIds){
            try {
                orderService.expireIfStale(orderId);
            } catch (RuntimeException e) {
                log.error("Failed to expire pending order (orderId={})", orderId, e);
            }
        }
    }
}
