package com.shopping_mall_api.service;

import com.shopping_mall_api.service.order.OrderExpirationScheduler;
import com.shopping_mall_api.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderExpirationSchedulerTest {
    @Mock private OrderService orderService;

    @InjectMocks private OrderExpirationScheduler scheduler;

    @Test
    void expiresEachOrder_andContinuesAfterFailure(){
        ReflectionTestUtils.setField(scheduler, "pendingExpirationMinutes", 30L);
        when(orderService.findStalePendingOrderIds(any(LocalDateTime.class), anyInt())).thenReturn(List.of(1L, 2L, 3L));
        doThrow(new RuntimeException("db error")).when(orderService).expireIfStale(2L);

        scheduler.expireStalePendingOrders();

        // 2번이 실패해도 3번까지 처리
        verify(orderService).expireIfStale(1L);
        verify(orderService).expireIfStale(2L);
        verify(orderService).expireIfStale(3L);
    }

    @Test
    void usesConfiguredThreshold(){
        ReflectionTestUtils.setField(scheduler, "pendingExpirationMinutes", 30L);
        when(orderService.findStalePendingOrderIds(any(LocalDateTime.class), anyInt())).thenReturn(List.of());

        LocalDateTime before = LocalDateTime.now().minusMinutes(30);
        scheduler.expireStalePendingOrders();
        LocalDateTime after = LocalDateTime.now().minusMinutes(30);

        verify(orderService).findStalePendingOrderIds(argThat(t -> !t.isBefore(before) && !t.isAfter(after)), eq(100));
        verify(orderService, never()).expireIfStale(any());
    }
}
