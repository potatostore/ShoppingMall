package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.order.OrderResponseDTO;
import com.shopping_mall_api.dto.order.OrderUpdateDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.global.constant.TableNames;
import com.shopping_mall_api.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "https://localhost:3000")
@RestController
@RequestMapping("/" + TableNames.orderTableName)
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@PathVariable Long userId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : create order",
                orderService.createOrder(userId)
        ));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrders(@PathVariable Long userId){
       return ResponseEntity.ok(ApiResponse.success(
                "Success : get all orders (" + userId + ")",
                orderService.getOrders(userId)
        ));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrder(@PathVariable Long orderId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get order (" + orderId + ")",
                orderService.getOrder(orderId)
        ));
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> patchOrder(
            @PathVariable Long orderId, @Valid @RequestBody OrderUpdateDTO orderUpdateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : patch order (" + orderId + ")",
                orderService.patchOrder(orderId, orderUpdateDTO)
        ));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> putOrder(
            @PathVariable Long orderId, @Valid @RequestBody OrderUpdateDTO orderUpdateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : put order (" + orderId + ")",
                orderService.putOrder(orderId, orderUpdateDTO)
        ));
    }

    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable Long orderId){
        orderService.deleteOrder(orderId);
    }

}
