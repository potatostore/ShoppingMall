package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.order.OrderResponseDTO;
import com.shopping_mall_api.dto.order.OrderUpdateDTO;
import com.shopping_mall_api.dto.order.orderItem.OrderItemCreateDTO;
import com.shopping_mall_api.dto.payment.toss.TossPaymentRequestDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "https://localhost:3000")
@RestController
@RequestMapping(ApiURLNames.orderURL)
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping(ApiURLNames.createOrderURL)
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody List<OrderItemCreateDTO> orderItemCreateDTOList){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : create order",
                orderService.createOrder(userId, orderItemCreateDTOList)
        ));
    }

    @PostMapping(ApiURLNames.tossPaymentAuthURL)
    public ResponseEntity<ApiResponse<OrderResponseDTO>> authTossPaymentOrder(
            @Valid @RequestBody TossPaymentRequestDTO tossPaymentRequestDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : paid order with toss",
                orderService.authTossPayment(tossPaymentRequestDTO)
        ));
    }

    @GetMapping(ApiURLNames.findOrdersURL)
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrders(){
       return ResponseEntity.ok(ApiResponse.success(
                "Success : get all orders ",
                orderService.getOrders()
        ));
    }

    @GetMapping(ApiURLNames.findOrdersWithUserIdURL)
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrdersWithUserId(@AuthenticationPrincipal Long userId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get all orders in user (" + userId + ")",
                orderService.getOrdersWithUserId(userId)
        ));
    }

    @GetMapping(ApiURLNames.findOrderURL)
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderWithUserId(
            @AuthenticationPrincipal Long userId, @PathVariable Long orderId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get order (" + orderId + ")",
                orderService.getOrderWithUserId(userId, orderId)
        ));
    }

    @PatchMapping(ApiURLNames.updateOrderURL)
    public ResponseEntity<ApiResponse<OrderResponseDTO>> patchOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateDTO orderUpdateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : patch order (" + orderId + ")",
                orderService.patchOrder(userId, orderId, orderUpdateDTO)
        ));
    }

    @DeleteMapping(ApiURLNames.deleteOrderURL)
    public void deleteOrder(@PathVariable Long orderId){
        orderService.deleteOrder(orderId);
    }

}
