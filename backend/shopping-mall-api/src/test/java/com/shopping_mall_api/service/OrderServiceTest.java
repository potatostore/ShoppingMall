package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.order.OrderCreateDTO;
import com.shopping_mall_api.dto.order.OrderResponseDTO;
import com.shopping_mall_api.dto.order.OrderUpdateDTO;
import com.shopping_mall_api.dto.order.orderItem.OrderItemCreateDTO;
import com.shopping_mall_api.dto.order.orderItem.OrderItemUpdateDTO;
import com.shopping_mall_api.dto.payment.toss.TossPaymentRequestDTO;
import com.shopping_mall_api.dto.payment.toss.response.Payment;
import com.shopping_mall_api.dto.payment.toss.response.TossError;
import com.shopping_mall_api.dto.payment.toss.response.TossResponse;
import com.shopping_mall_api.entity.order.Order;
import com.shopping_mall_api.entity.order.OrderStatus;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.client.toss.TossClient;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.global.exception.PaymentException;
import com.shopping_mall_api.global.exception.UnmatchedPriceException;
import com.shopping_mall_api.repository.order.OrderRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private TossClient tossClient;

    @InjectMocks private OrderService orderService;

    @Test
    void createOrder_successTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        OrderItemCreateDTO orderItemCreateDTO = OrderItemCreateDTO.builder()
                                                    .productId(1L)
                                                    .curOrderItemPrice(20260901L)
                                                    .quantity(1L)
                                                    .build();

        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(
                List.of(orderItemCreateDTO)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));

        OrderResponseDTO result = orderService.createOrder(1L, orderCreateDTO);

        assertThat(result.getTotalOrderPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getQuantity()).isEqualTo(1L);
    }

    @Test
    void authTossPayment_successTest(){
        TossPaymentRequestDTO tossPaymentRequestDTO = new TossPaymentRequestDTO(
                "toss-test-payment-key",
                1L,
                20260901L
        );

        TossResponse tossResponse = mock(Payment.class);

        OrderItemCreateDTO orderItemCreateDTO = OrderItemCreateDTO.builder()
                .productId(1L)
                .curOrderItemPrice(20260901L)
                .quantity(1L)
                .build();

        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        Order existOrder = Order.builder()
                .user(existUser)
                .orderStatus(OrderStatus.PENDING)
                .orderItemCreateDTOList(List.of(orderItemCreateDTO))
                .build();

        when(orderRepository.findByOrderUid(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.confirm(tossPaymentRequestDTO)).thenReturn(tossResponse);

        OrderResponseDTO result = orderService.authTossPayment(tossPaymentRequestDTO);
        
        assertThat(result.getOrderItemResponseDTOList().getFirst().getCurOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getQuantity()).isEqualTo(1L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getCurOrderItemPrice()).isEqualTo(20260901L);
    }

    @Test
    void authTossPayment_orderNotFoundExceptionTest(){
        TossPaymentRequestDTO tossPaymentRequestDTO = new TossPaymentRequestDTO(
                "toss-test-payment-key",
                999L,
                20260901L
        );

        when(orderRepository.findByOrderUid(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.authTossPayment(tossPaymentRequestDTO));
    }

    @Test
    void authTossPayment_tossErrorObjectExceptionTest(){
        TossPaymentRequestDTO tossPaymentRequestDTO = new TossPaymentRequestDTO(
                "toss-test-payment-key",
                1L,
                20260901L
        );
        
        TossResponse tossResponse = mock(TossError.class);

        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        OrderItemCreateDTO orderItemCreateDTO = OrderItemCreateDTO.builder()
                .productId(1L)
                .curOrderItemPrice(20260901L)
                .quantity(1L)
                .build();

        Order existOrder = Order.builder()
                        .user(existUser)
                        .orderStatus(OrderStatus.PENDING)
                        .orderItemCreateDTOList(List.of(orderItemCreateDTO))
                        .build();

        when(orderRepository.findByOrderUid(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.confirm(tossPaymentRequestDTO)).thenReturn(tossResponse);

        assertThrows(PaymentException.class, () -> orderService.authTossPayment(tossPaymentRequestDTO));
    }

    @Test
    void authTossPayment_amountUnmatchedExceptionTest(){
        TossPaymentRequestDTO tossPaymentRequestDTO = new TossPaymentRequestDTO(
                "toss-test-payment-key",
                1L,
                2026090101L
        );
        
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        OrderItemCreateDTO orderItemCreateDTO = OrderItemCreateDTO.builder()
                .productId(1L)
                .curOrderItemPrice(20260901L)
                .quantity(1L)
                .build();

        Order existOrder = Order.builder()
                .user(existUser)
                .orderStatus(OrderStatus.PENDING)
                .orderItemCreateDTOList(List.of(orderItemCreateDTO))
                .build();
        
        when(orderRepository.findByOrderUid(1L)).thenReturn(Optional.of(existOrder));
        
        assertThrows(UnmatchedPriceException.class, () -> orderService.authTossPayment(tossPaymentRequestDTO));
    }

    @Test
    void getOrders_successTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        OrderItemCreateDTO orderItemCreateDTO = new OrderItemCreateDTO(
                1L,
                20260901L,
                1L
        );

        Order existOrder = Order.builder()
                        .user(existUser)
                        .orderItemCreateDTOList(List.of(orderItemCreateDTO))
                        .orderStatus(OrderStatus.PENDING)
                        .build();

        when(orderRepository.findAll()).thenReturn(List.of(existOrder));

        List<OrderResponseDTO> resultList = orderService.getOrders();
        OrderResponseDTO result = resultList.getFirst();

        assertThat(result.getTotalOrderPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getCurOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getTotalOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getQuantity()).isEqualTo(1L);
    }

    @Test
    void getOrdersWithUserId_successTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        OrderItemCreateDTO orderItemCreateDTO = new OrderItemCreateDTO(
                1L,
                20260901L,
                1L
        );

        Order existOrder = Order.builder()
                .user(existUser)
                .orderItemCreateDTOList(List.of(orderItemCreateDTO))
                .orderStatus(OrderStatus.PENDING)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));
        when(orderRepository.findByUser_UserId(1L)).thenReturn(List.of(existOrder));

        List<OrderResponseDTO> resultList = orderService.getOrdersWithUserId(1L);
        OrderResponseDTO result = resultList.getFirst();
        
        assertThat(result.getTotalOrderPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getCurOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getTotalOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getQuantity()).isEqualTo(1L);
    }

    @Test
    void getOrdersWithUserId_userNotFoundExceptionTest(){
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrdersWithUserId(999L));
    }
    
    @Test
    void getOrderWithUserId_successTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        OrderItemCreateDTO orderItemCreateDTO = new OrderItemCreateDTO(
                1L,
                20260901L,
                1L
        );

        Order existOrder = Order.builder()
                .user(existUser)
                .orderItemCreateDTOList(List.of(orderItemCreateDTO))
                .orderStatus(OrderStatus.PENDING)
                .build();

        ReflectionTestUtils.setField(existOrder, "orderId", 1L);

        when(orderRepository.findByUser_UserId(1L)).thenReturn(List.of(existOrder));

        OrderResponseDTO result = orderService.getOrderWithUserId(1L, 1L);

        assertThat(result.getTotalOrderPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getCurOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getTotalOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getQuantity()).isEqualTo(1L);
    }
    
    @Test
    void getOrderWithUserId_orderNotFoundExceptionTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        OrderItemCreateDTO orderItemCreateDTO = new OrderItemCreateDTO(
                1L,
                20260901L,
                1L
        );

        Order existOrder = Order.builder()
                .user(existUser)
                .orderItemCreateDTOList(List.of(orderItemCreateDTO))
                .orderStatus(OrderStatus.PENDING)
                .build();

        ReflectionTestUtils.setField(existOrder, "orderId", 1L);

        when(orderRepository.findByUser_UserId(999L)).thenReturn(List.of(existOrder));

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithUserId(999L, 999L));
    }

    @Test
    void patchOrder_successTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        ReflectionTestUtils.setField(existUser, "userId", 1L);

        OrderItemCreateDTO orderItemCreateDTO = new OrderItemCreateDTO(
                1L,
                20260901L,
                1L
        );

        Order existOrder = Order.builder()
                .user(existUser)
                .orderItemCreateDTOList(List.of(orderItemCreateDTO))
                .orderStatus(OrderStatus.PENDING)
                .build();

        OrderItemUpdateDTO orderItemUpdateDTO = new OrderItemUpdateDTO(
                1L,
                2026090101L,
                3L
        );

        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(List.of(orderItemUpdateDTO));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existOrder));

        OrderResponseDTO result = orderService.patchOrder(1L, 1L, orderUpdateDTO);

        assertThat(result.getTotalOrderPrice()).isEqualTo(2026090101L * 3L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getCurOrderItemPrice()).isEqualTo(2026090101L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getTotalOrderItemPrice()).isEqualTo(2026090101L * 3L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getQuantity()).isEqualTo(3L);
    }

    @Test
    void patchOrder_orderNotFoundExceptionTest(){
        OrderItemUpdateDTO orderItemUpdateDTO = new OrderItemUpdateDTO(
                1L,
                2026090101L,
                3L
        );

        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(List.of(orderItemUpdateDTO));

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.patchOrder(999L, 999L, orderUpdateDTO));
    }

    @Test
    void deleteOrder_successTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        OrderItemCreateDTO orderItemCreateDTO = OrderItemCreateDTO.builder()
                .productId(1L)
                .quantity(1L)
                .curOrderItemPrice(20260901L)
                .build();

        Order existOrder = Order.builder()
                .user(existUser)
                .orderItemCreateDTOList(List.of(orderItemCreateDTO))
                .orderStatus(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existOrder));

        orderService.deleteOrder(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteOrder_orderNotFoundExceptionTest(){
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.deleteOrder(999L));
    }
}
