package com.shopping_mall_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_mall_api.dto.order.OrderCreateDTO;
import com.shopping_mall_api.dto.order.OrderResponseDTO;
import com.shopping_mall_api.dto.order.OrderUpdateDTO;
import com.shopping_mall_api.dto.order.orderItem.OrderItemCreateDTO;
import com.shopping_mall_api.dto.order.orderItem.OrderItemResponseDTO;
import com.shopping_mall_api.dto.order.orderItem.OrderItemUpdateDTO;
import com.shopping_mall_api.dto.payment.toss.TossPaymentRequestDTO;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private OrderService orderService;

    @Test
    void createOrder_successTest() throws Exception{
        Long userId = 1L;
        Long productId = 1L;
        Long orderId = 1L;
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        OrderItemCreateDTO orderItemCreateDTO = OrderItemCreateDTO.builder()
                .productId(productId)
                .quantity(1L)
                .curOrderItemPrice(20260908L)
                .build();

        OrderCreateDTO orderCreateDTO = OrderCreateDTO.builder()
                .orderItemCreateDTOList(List.of(orderItemCreateDTO))
                .build();

        OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO(
                productId,
                20260908L,
                1L,
                20260908L
        );

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                orderId,
                orderId,
                List.of(orderItemResponseDTO),
                20260908L
        );

        when(orderService.createOrder(any(Long.class), any(OrderCreateDTO.class))).thenReturn(orderResponseDTO);

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(post(ApiURLNames.orderURL + ApiURLNames.createOrderURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : create order"))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].curOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].quantity").value(1L))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].totalOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data.totalOrderPrice").value(20260908L));
    }

    @Test
    void authTossPayment_successTest() throws Exception{
        Long orderId = 1L;
        Long productId = 1L;
        OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO(
                productId,
                20260908L,
                1L,
                20260908L
        );

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                orderId,
                orderId,
                List.of(orderItemResponseDTO),
                20260908L
        );

        TossPaymentRequestDTO tossPaymentRequestDTO = TossPaymentRequestDTO.builder()
                .paymentKey("test-payment-key")
                .orderId(orderId)
                .amount(20260908L)
                .build();

        when(orderService.authTossPayment(any(TossPaymentRequestDTO.class))).thenReturn(orderResponseDTO);

        mockMvc.perform(post(ApiURLNames.orderURL + ApiURLNames.tossPaymentAuthURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tossPaymentRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : paid order with toss"))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].curOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].quantity").value(1L))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].totalOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data.totalOrderPrice").value(20260908L));
    }

    @Test
    void getOrders_successTest() throws Exception{
        Long orderId = 1L;
        Long productId = 1L;
        OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO(
                productId,
                20260908L,
                1L,
                20260908L
        );

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                orderId,
                orderId,
                List.of(orderItemResponseDTO),
                20260908L
        );

        when(orderService.getOrders()).thenReturn(List.of(orderResponseDTO));

        mockMvc.perform(get(ApiURLNames.orderURL + ApiURLNames.findOrdersURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : get all orders"))
                .andExpect(jsonPath("$.data[0].orderItemResponseDTOList[0].curOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data[0].orderItemResponseDTOList[0].quantity").value(1L))
                .andExpect(jsonPath("$.data[0].orderItemResponseDTOList[0].totalOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data[0].totalOrderPrice").value(20260908L));
    }

    @Test
    void getOrdersWithUserId_successTest() throws Exception{
        Long userId = 1L;
        Long orderId = 1L;
        Long productId = 1L;
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO(
                productId,
                20260908L,
                1L,
                20260908L
        );

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                orderId,
                orderId,
                List.of(orderItemResponseDTO),
                20260908L
        );

        when(orderService.getOrdersWithUserId(any(Long.class))).thenReturn(List.of(orderResponseDTO));

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get(ApiURLNames.orderURL + ApiURLNames.findOrdersWithUserIdURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : get all orders in user (1)"))
                .andExpect(jsonPath("$.data[0].orderItemResponseDTOList[0].curOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data[0].orderItemResponseDTOList[0].quantity").value(1L))
                .andExpect(jsonPath("$.data[0].orderItemResponseDTOList[0].totalOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data[0].totalOrderPrice").value(20260908L));
    }

    @Test
    void getOrderWithUserId_successTest() throws Exception{
        Long userId = 1L;
        Long orderId = 1L;
        Long productId = 1L;

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO(
                productId,
                20260908L,
                1L,
                20260908L
        );

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                orderId,
                orderId,
                List.of(orderItemResponseDTO),
                20260908L
        );

        when(orderService.getOrderWithUserId(userId, orderId)).thenReturn(orderResponseDTO);

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get(ApiURLNames.orderURL + ApiURLNames.findOrderURL, orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : get order (1)"))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].curOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].quantity").value(1L))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].totalOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data.totalOrderPrice").value(20260908L));
    }

    @Test
    void patchOrder_successTest() throws Exception{
        Long userId = 1L;
        Long orderId = 1L;
        Long productId = 1L;

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO(
                productId,
                20260908L,
                1L,
                20260908L
        );

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                orderId,
                orderId,
                List.of(orderItemResponseDTO),
                20260908L
        );

        OrderItemUpdateDTO orderItemUpdateDTO = OrderItemUpdateDTO.builder()
                .productId(productId)
                .curOrderItemPrice(20260908L)
                .quantity(1L)
                .build();

        OrderUpdateDTO orderUpdateDTO = OrderUpdateDTO.builder()
                .orderItemResponseDTOList(List.of(orderItemUpdateDTO))
                .build();

        when(orderService.patchOrder(any(Long.class), any(Long.class), any(OrderUpdateDTO.class)))
                .thenReturn(orderResponseDTO);

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(patch(ApiURLNames.orderURL + ApiURLNames.updateOrderURL, orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : patch order (1)"))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].curOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].quantity").value(1L))
                .andExpect(jsonPath("$.data.orderItemResponseDTOList[0].totalOrderItemPrice").value(20260908L))
                .andExpect(jsonPath("$.data.totalOrderPrice").value(20260908L));
    }

    @Test
    void deleteOrder_successTest() throws Exception{
        Long orderId = 1L;

        mockMvc.perform(delete(ApiURLNames.orderURL + ApiURLNames.deleteOrderURL, orderId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(orderService).deleteOrder(orderId);
    }
}
