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
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.entity.cart.Cart;
import com.shopping_mall_api.entity.order.Order;
import com.shopping_mall_api.entity.order.OrderItem;
import com.shopping_mall_api.entity.order.OrderStatus;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.client.toss.TossClient;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.InvalidOrderStatusException;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.global.exception.OutOfStockException;
import com.shopping_mall_api.global.exception.PaymentException;
import com.shopping_mall_api.global.exception.UnmatchedPriceException;
import com.shopping_mall_api.repository.cart.CartRepository;
import com.shopping_mall_api.repository.order.OrderRepository;
import com.shopping_mall_api.repository.product.ProductRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartRepository cartRepository;
    @Mock private TossClient tossClient;

    @InjectMocks private OrderService orderService;

    private User createUser(){
        User user = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        ReflectionTestUtils.setField(user, "userId", 1L);

        return user;
    }

    private Product createProduct(Long productId, Long price){
        Product product = Product.builder()
                .name("상품" + productId)
                .price(price)
                .stock(100L)
                .productDetailCreateDTOList(List.of(new ProductDetailCreateDTO("detail")))
                .build();

        ReflectionTestUtils.setField(product, "productId", productId);

        return product;
    }

    @Test
    void createOrder_successTest(){
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(List.of(
                OrderItemCreateDTO.builder().productId(1L).quantity(2L).build(),
                OrderItemCreateDTO.builder().productId(2L).quantity(1L).build()
        ));

        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));
        when(productRepository.findAllById(any())).thenReturn(List.of(
                createProduct(1L, 1000L),
                createProduct(2L, 3000L)
        ));
        when(productRepository.decreaseStock(anyLong(), anyLong())).thenReturn(1);

        OrderResponseDTO result = orderService.createOrder(1L, orderCreateDTO);

        // 가격은 Product.price 기준: 1000 * 2 + 3000 * 1
        assertThat(result.getTotalOrderPrice()).isEqualTo(5000L);
        assertThat(result.getOrderItemResponseDTOList())
                .extracting("productId", "curOrderItemPrice", "quantity")
                .containsExactly(tuple(1L, 1000L, 2L), tuple(2L, 3000L, 1L));
        verify(orderRepository).save(any(Order.class));
        // productId 오름차순으로 재고 차감
        InOrder inOrder = inOrder(productRepository);
        inOrder.verify(productRepository).decreaseStock(1L, 2L);
        inOrder.verify(productRepository).decreaseStock(2L, 1L);
    }

    @Test
    void createOrder_outOfStockExceptionTest(){
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(List.of(
                OrderItemCreateDTO.builder().productId(1L).quantity(5L).build()
        ));

        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));
        when(productRepository.findAllById(any())).thenReturn(List.of(createProduct(1L, 1000L)));
        when(productRepository.decreaseStock(1L, 5L)).thenReturn(0);

        assertThrows(OutOfStockException.class, () -> orderService.createOrder(1L, orderCreateDTO));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_mergeSameProductTest(){
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(List.of(
                OrderItemCreateDTO.builder().productId(1L).quantity(1L).build(),
                OrderItemCreateDTO.builder().productId(1L).quantity(2L).build()
        ));

        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));
        when(productRepository.findAllById(any())).thenReturn(List.of(createProduct(1L, 1000L)));
        when(productRepository.decreaseStock(1L, 3L)).thenReturn(1);

        OrderResponseDTO result = orderService.createOrder(1L, orderCreateDTO);

        assertThat(result.getOrderItemResponseDTOList()).hasSize(1);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getQuantity()).isEqualTo(3L);
        assertThat(result.getTotalOrderPrice()).isEqualTo(3000L);
    }

    @Test
    void createOrder_productNotFoundExceptionTest(){
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(List.of(
                OrderItemCreateDTO.builder().productId(999L).quantity(1L).build()
        ));

        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));
        when(productRepository.findAllById(any())).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> orderService.createOrder(1L, orderCreateDTO));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void authTossPayment_successTest(){
        TossPaymentRequestDTO tossPaymentRequestDTO = new TossPaymentRequestDTO(
                "toss-test-payment-key",
                1L,
                20260901L
        );

        TossResponse tossResponse = mock(Payment.class);

        OrderItem orderItem = OrderItem.builder()
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
                .orderItemList(List.of(orderItem))
                .build();

        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.confirm(tossPaymentRequestDTO)).thenReturn(tossResponse);

        OrderResponseDTO result = orderService.authTossPayment(tossPaymentRequestDTO);

        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getCurOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getQuantity()).isEqualTo(1L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getCurOrderItemPrice()).isEqualTo(20260901L);
    }

    @Test
    void authTossPayment_removesPaidItemsFromCartTest(){
        Cart cart = Cart.builder().userId(1L).build();
        cart.addCartItemInCart(createProduct(1L, 1000L), 1L);
        cart.addCartItemInCart(createProduct(2L, 3000L), 2L);

        Order existOrder = Order.builder()
                .user(createUser())
                .orderItemList(List.of(new OrderItem(1L, 1000L, 1L)))
                .orderStatus(OrderStatus.PENDING)
                .build();

        TossPaymentRequestDTO tossPaymentRequestDTO = new TossPaymentRequestDTO("toss-test-payment-key", 1L, 1000L);

        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.confirm(tossPaymentRequestDTO)).thenReturn(mock(Payment.class));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        orderService.authTossPayment(tossPaymentRequestDTO);

        // 결제한 1번 상품만 빠지고, 주문하지 않은 2번 상품은 남음
        assertThat(cart.getCartItemList())
                .extracting(cartItem -> cartItem.getProduct().getProductId())
                .containsExactly(2L);
        assertThat(cart.getTotalCartPrice()).isEqualTo(6000L);
    }

    @Test
    void authTossPayment_tossErrorKeepsCartTest(){
        Order existOrder = Order.builder()
                .user(createUser())
                .orderItemList(List.of(new OrderItem(1L, 1000L, 1L)))
                .orderStatus(OrderStatus.PENDING)
                .build();

        TossPaymentRequestDTO tossPaymentRequestDTO = new TossPaymentRequestDTO("toss-test-payment-key", 1L, 1000L);

        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.confirm(tossPaymentRequestDTO)).thenReturn(mock(TossError.class));

        assertThrows(PaymentException.class, () -> orderService.authTossPayment(tossPaymentRequestDTO));
        verify(cartRepository, never()).findByUserId(any());
    }

    private Order pendingOrder(){
        return Order.builder()
                .user(createUser())
                .orderItemList(List.of(new OrderItem(1L, 1000L, 1L)))
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    private Payment tossPayment(String status){
        Payment payment = mock(Payment.class);
        when(payment.status()).thenReturn(status);
        return payment;
    }

    @Test
    void authTossPayment_networkErrorButTossApproved_completesTest(){
        Order existOrder = pendingOrder();
        TossPaymentRequestDTO request = new TossPaymentRequestDTO("toss-test-payment-key", 1L, 1000L);
        Payment done = tossPayment("DONE");
        when(done.paymentKey()).thenReturn("pk-1");

        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.confirm(request)).thenThrow(new ResourceAccessException("Read timed out"));
        when(tossClient.getPaymentByOrderId(existOrder.getOrderUid())).thenReturn(done);

        orderService.authTossPayment(request);

        // 응답은 유실됐지만 토스에서 승인 완료(DONE) → 결제 완료 처리(조회 결과의 paymentKey 저장), 재고는 그대로
        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(existOrder.getPaymentKey()).isEqualTo("pk-1");
        verify(productRepository, never()).increaseStock(anyLong(), anyLong());
    }

    @Test
    void authTossPayment_networkErrorAndLookupFails_keepsPendingTest(){
        Order existOrder = pendingOrder();
        TossPaymentRequestDTO request = new TossPaymentRequestDTO("toss-test-payment-key", 1L, 1000L);

        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.confirm(request)).thenThrow(new ResourceAccessException("Read timed out"));
        when(tossClient.getPaymentByOrderId(existOrder.getOrderUid())).thenThrow(new ResourceAccessException("Connect timed out"));

        PaymentException e = assertThrows(PaymentException.class, () -> orderService.authTossPayment(request));

        // 실제 상태를 모르므로 실패로 단정하지 않음: PENDING 유지, 재고 유지, 502
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.TOSS_SERVER_ERROR);
        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        verify(productRepository, never()).increaseStock(anyLong(), anyLong());
    }

    @Test
    void authTossPayment_networkErrorAndPaymentNotFound_keepsPendingTest(){
        Order existOrder = pendingOrder();
        TossPaymentRequestDTO request = new TossPaymentRequestDTO("toss-test-payment-key", 1L, 1000L);

        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.confirm(request)).thenThrow(new ResourceAccessException("Connect timed out"));
        when(tossClient.getPaymentByOrderId(existOrder.getOrderUid()))
                .thenReturn(new TossError("NOT_FOUND_PAYMENT", "존재하지 않는 결제 정보 입니다."));

        PaymentException e = assertThrows(PaymentException.class, () -> orderService.authTossPayment(request));

        // 승인 요청이 토스에 닿지 않음 → 다시 승인 요청할 수 있도록 PENDING 유지
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.TOSS_SERVER_ERROR);
        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void authTossPayment_networkErrorAndPaymentAborted_failsAndRestoresStockTest(){
        Order existOrder = pendingOrder();
        TossPaymentRequestDTO request = new TossPaymentRequestDTO("toss-test-payment-key", 1L, 1000L);
        Payment aborted = tossPayment("ABORTED");

        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.confirm(request)).thenThrow(new ResourceAccessException("Read timed out"));
        when(tossClient.getPaymentByOrderId(existOrder.getOrderUid())).thenReturn(aborted);

        PaymentException e = assertThrows(PaymentException.class, () -> orderService.authTossPayment(request));

        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_FAILED);
        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.FAILED);
        verify(productRepository).increaseStock(1L, 1L);
    }

    @Test
    void authTossPayment_alreadyPaidOrderTest(){
        Order existOrder = Order.builder()
                .user(createUser())
                .orderItemList(List.of(new OrderItem(1L, 20260901L, 1L)))
                .orderStatus(OrderStatus.PENDING)
                .build();
        existOrder.completePayment("test-payment-key");

        TossPaymentRequestDTO tossPaymentRequestDTO = new TossPaymentRequestDTO("toss-test-payment-key", 1L, 20260901L);

        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));

        OrderResponseDTO result = orderService.authTossPayment(tossPaymentRequestDTO);

        // 재승인 요청은 토스를 다시 호출하지 않고 PAID 상태를 유지한 채 현재 주문을 반환
        assertThat(result.getTotalOrderPrice()).isEqualTo(20260901L);
        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        verify(tossClient, never()).confirm(any());
    }

    @Test
    void authTossPayment_failedOrderExceptionTest(){
        Order existOrder = Order.builder()
                .user(createUser())
                .orderItemList(List.of(new OrderItem(1L, 20260901L, 1L)))
                .orderStatus(OrderStatus.FAILED)
                .build();

        TossPaymentRequestDTO tossPaymentRequestDTO = new TossPaymentRequestDTO("toss-test-payment-key", 1L, 20260901L);

        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.authTossPayment(tossPaymentRequestDTO));
        verify(tossClient, never()).confirm(any());
    }

    @Test
    void authTossPayment_orderNotFoundExceptionTest(){
        TossPaymentRequestDTO tossPaymentRequestDTO = new TossPaymentRequestDTO(
                "toss-test-payment-key",
                999L,
                20260901L
        );

        when(orderRepository.findByOrderUidForUpdate(999L)).thenReturn(Optional.empty());

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

        OrderItem orderItem = OrderItem.builder()
                .productId(1L)
                .curOrderItemPrice(20260901L)
                .quantity(1L)
                .build();

        Order existOrder = Order.builder()
                        .user(existUser)
                        .orderStatus(OrderStatus.PENDING)
                        .orderItemList(List.of(orderItem))
                        .build();

        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.confirm(tossPaymentRequestDTO)).thenReturn(tossResponse);

        assertThrows(PaymentException.class, () -> orderService.authTossPayment(tossPaymentRequestDTO));
        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.FAILED);
        verify(productRepository).increaseStock(1L, 1L);
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

        OrderItem orderItem = OrderItem.builder()
                .productId(1L)
                .curOrderItemPrice(20260901L)
                .quantity(1L)
                .build();

        Order existOrder = Order.builder()
                .user(existUser)
                .orderStatus(OrderStatus.PENDING)
                .orderItemList(List.of(orderItem))
                .build();
        
        when(orderRepository.findByOrderUidForUpdate(1L)).thenReturn(Optional.of(existOrder));
        
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

        OrderItem orderItem = new OrderItem(
                1L,
                20260901L,
                1L
        );

        Order existOrder = Order.builder()
                        .user(existUser)
                        .orderItemList(List.of(orderItem))
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

        OrderItem orderItem = new OrderItem(
                1L,
                20260901L,
                1L
        );

        Order existOrder = Order.builder()
                .user(existUser)
                .orderItemList(List.of(orderItem))
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

        OrderItem orderItem = new OrderItem(
                1L,
                20260901L,
                1L
        );

        Order existOrder = Order.builder()
                .user(existUser)
                .orderItemList(List.of(orderItem))
                .orderStatus(OrderStatus.PENDING)
                .build();

        ReflectionTestUtils.setField(existOrder, "orderId", 1L);

        when(orderRepository.findByOrderIdAndUser_UserId(1L, 1L)).thenReturn(Optional.of(existOrder));

        OrderResponseDTO result = orderService.getOrderWithUserId(1L, 1L);

        assertThat(result.getTotalOrderPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getCurOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getTotalOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getQuantity()).isEqualTo(1L);
    }
    
    @Test
    void getOrderWithUserId_orderNotFoundExceptionTest(){
        // 다른 사용자(999)가 1번 주문을 조회하면 404
        when(orderRepository.findByOrderIdAndUser_UserId(1L, 999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderWithUserId(999L, 1L));
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

        OrderItem orderItem = new OrderItem(
                1L,
                20260901L,
                1L
        );

        Order existOrder = Order.builder()
                .user(existUser)
                .orderItemList(List.of(orderItem))
                .orderStatus(OrderStatus.PENDING)
                .build();

        OrderItemUpdateDTO orderItemUpdateDTO = new OrderItemUpdateDTO(
                1L,
                3L
        );

        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(List.of(orderItemUpdateDTO));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existOrder));
        when(productRepository.decreaseStock(1L, 2L)).thenReturn(1);

        OrderResponseDTO result = orderService.patchOrder(1L, 1L, orderUpdateDTO);

        // 늘어난 수량(1 → 3)만큼만 재고 차감
        verify(productRepository).decreaseStock(1L, 2L);
        // 가격은 주문 시점 가격 유지, 수량만 변경
        assertThat(result.getTotalOrderPrice()).isEqualTo(20260901L * 3L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getCurOrderItemPrice()).isEqualTo(20260901L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getTotalOrderItemPrice()).isEqualTo(20260901L * 3L);
        assertThat(result.getOrderItemResponseDTOList().getFirst().getQuantity()).isEqualTo(3L);
    }

    @Test
    void patchOrder_decreaseQuantityRestoresStockTest(){
        Order existOrder = Order.builder()
                .user(createUser())
                .orderItemList(List.of(new OrderItem(1L, 1000L, 5L)))
                .orderStatus(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existOrder));

        orderService.patchOrder(1L, 1L, new OrderUpdateDTO(List.of(new OrderItemUpdateDTO(1L, 2L))));

        verify(productRepository).increaseStock(1L, 3L);
        verify(productRepository, never()).decreaseStock(anyLong(), anyLong());
    }

    @Test
    void patchOrder_increaseQuantityOutOfStockTest(){
        Order existOrder = Order.builder()
                .user(createUser())
                .orderItemList(List.of(new OrderItem(1L, 1000L, 1L)))
                .orderStatus(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existOrder));
        when(productRepository.decreaseStock(1L, 9L)).thenReturn(0);

        assertThrows(OutOfStockException.class,
                () -> orderService.patchOrder(1L, 1L, new OrderUpdateDTO(List.of(new OrderItemUpdateDTO(1L, 10L)))));
    }

    @Test
    void patchOrder_notPendingExceptionTest(){
        Order existOrder = Order.builder()
                .user(createUser())
                .orderItemList(List.of(new OrderItem(1L, 1000L, 1L)))
                .orderStatus(OrderStatus.PAID)
                .build();

        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(List.of(new OrderItemUpdateDTO(1L, 3L)));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existOrder));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.patchOrder(1L, 1L, orderUpdateDTO));
        assertThat(existOrder.getOrderItemList().getFirst().getQuantity()).isEqualTo(1L);
    }

    @Test
    void patchOrder_orderNotFoundExceptionTest(){
        OrderItemUpdateDTO orderItemUpdateDTO = new OrderItemUpdateDTO(
                1L,
                3L
        );

        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(List.of(orderItemUpdateDTO));

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.patchOrder(999L, 999L, orderUpdateDTO));
    }

    private Order paidOrder(){
        Order order = pendingOrder();
        order.completePayment("pk-1");
        return order;
    }

    @Test
    void cancelOrder_pendingCancelsAndRestoresStockTest(){
        Order existOrder = pendingOrder();

        when(orderRepository.findByOrderIdAndUserIdForUpdate(1L, 1L)).thenReturn(Optional.of(existOrder));

        OrderResponseDTO result = orderService.cancelOrder(1L, 1L, null);

        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(productRepository).increaseStock(1L, 1L);
        // 결제 전이라 토스 호출 없음
        verifyNoInteractions(tossClient);
    }

    @Test
    void cancelOrder_paidRefundsWithTossTest(){
        Order existOrder = paidOrder();

        Payment canceled = tossPayment("CANCELED");

        when(orderRepository.findByOrderIdAndUserIdForUpdate(1L, 1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.cancel(eq("pk-1"), eq("단순 변심"), eq("cancel-" + existOrder.getOrderUid())))
                .thenReturn(canceled);

        OrderResponseDTO result = orderService.cancelOrder(1L, 1L, "단순 변심");

        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(productRepository).increaseStock(1L, 1L);
    }

    @Test
    void cancelOrder_paidWithoutReasonUsesDefaultTest(){
        Order existOrder = paidOrder();

        Payment canceled = tossPayment("CANCELED");

        when(orderRepository.findByOrderIdAndUserIdForUpdate(1L, 1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.cancel(eq("pk-1"), eq("고객 요청에 의한 주문 취소"), any())).thenReturn(canceled);

        orderService.cancelOrder(1L, 1L, " ");

        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void cancelOrder_tossRejected_keepsPaidTest(){
        Order existOrder = paidOrder();

        when(orderRepository.findByOrderIdAndUserIdForUpdate(1L, 1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.cancel(any(), any(), any())).thenReturn(new TossError("NOT_CANCELABLE_PAYMENT", "취소 할 수 없는 결제 입니다."));

        PaymentException e = assertThrows(PaymentException.class, () -> orderService.cancelOrder(1L, 1L, null));

        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_CANCEL_FAILED);
        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        verify(productRepository, never()).increaseStock(anyLong(), anyLong());
    }

    @Test
    void cancelOrder_unexpectedTossStatus_keepsPaidTest(){
        Order existOrder = paidOrder();
        Payment partial = tossPayment("PARTIAL_CANCELED");

        when(orderRepository.findByOrderIdAndUserIdForUpdate(1L, 1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.cancel(any(), any(), any())).thenReturn(partial);

        assertThrows(PaymentException.class, () -> orderService.cancelOrder(1L, 1L, null));
        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void cancelOrder_tossNetworkError_keepsPaidTest(){
        Order existOrder = paidOrder();

        when(orderRepository.findByOrderIdAndUserIdForUpdate(1L, 1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.cancel(any(), any(), any())).thenThrow(new ResourceAccessException("Read timed out"));

        PaymentException e = assertThrows(PaymentException.class, () -> orderService.cancelOrder(1L, 1L, null));

        // 같은 Idempotency-Key로 재시도할 수 있도록 PAID 유지
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.TOSS_SERVER_ERROR);
        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void cancelOrder_alreadyCanceledTest(){
        Order existOrder = pendingOrder();
        existOrder.cancel();

        when(orderRepository.findByOrderIdAndUserIdForUpdate(1L, 1L)).thenReturn(Optional.of(existOrder));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.cancelOrder(1L, 1L, null));
        verify(productRepository, never()).increaseStock(anyLong(), anyLong());
    }

    @Test
    void cancelOrder_otherUsersOrderTest(){
        when(orderRepository.findByOrderIdAndUserIdForUpdate(1L, 999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.cancelOrder(999L, 1L, null));
    }

    @Test
    void expireIfStale_noPaymentAtToss_expiresAndRestoresStockTest(){
        Order existOrder = pendingOrder();

        when(orderRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.getPaymentByOrderId(existOrder.getOrderUid()))
                .thenReturn(new TossError("NOT_FOUND_PAYMENT", "존재하지 않는 결제 정보 입니다."));

        orderService.expireIfStale(1L);

        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.EXPIRED);
        verify(productRepository).increaseStock(1L, 1L);
    }

    @Test
    void expireIfStale_tossAborted_expiresTest(){
        Order existOrder = pendingOrder();
        Payment aborted = tossPayment("ABORTED");

        when(orderRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.getPaymentByOrderId(existOrder.getOrderUid())).thenReturn(aborted);

        orderService.expireIfStale(1L);

        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.EXPIRED);
        verify(productRepository).increaseStock(1L, 1L);
    }

    @Test
    void expireIfStale_tossDone_completesPaymentTest(){
        Order existOrder = pendingOrder();
        Payment done = tossPayment("DONE");
        when(done.paymentKey()).thenReturn("pk-late");

        when(orderRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.getPaymentByOrderId(existOrder.getOrderUid())).thenReturn(done);

        orderService.expireIfStale(1L);

        // 승인 응답만 유실됐던 주문 → 만료가 아니라 결제 완료로 복구
        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(existOrder.getPaymentKey()).isEqualTo("pk-late");
        verify(productRepository, never()).increaseStock(anyLong(), anyLong());
    }

    @Test
    void expireIfStale_tossInProgress_keepsPendingTest(){
        Order existOrder = pendingOrder();
        Payment inProgress = tossPayment("IN_PROGRESS");

        when(orderRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.getPaymentByOrderId(existOrder.getOrderUid())).thenReturn(inProgress);

        orderService.expireIfStale(1L);

        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        verify(productRepository, never()).increaseStock(anyLong(), anyLong());
    }

    @Test
    void expireIfStale_lookupFails_keepsPendingTest(){
        Order existOrder = pendingOrder();

        when(orderRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existOrder));
        when(tossClient.getPaymentByOrderId(existOrder.getOrderUid())).thenThrow(new ResourceAccessException("timeout"));

        orderService.expireIfStale(1L);

        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        verify(productRepository, never()).increaseStock(anyLong(), anyLong());
    }

    @Test
    void expireIfStale_alreadyPaid_skipsTest(){
        Order existOrder = paidOrder();

        when(orderRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existOrder));

        orderService.expireIfStale(1L);

        assertThat(existOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        verifyNoInteractions(tossClient);
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

        OrderItem orderItem = OrderItem.builder()
                .productId(1L)
                .quantity(1L)
                .curOrderItemPrice(20260901L)
                .build();

        Order existOrder = Order.builder()
                .user(existUser)
                .orderItemList(List.of(orderItem))
                .orderStatus(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existOrder));

        orderService.deleteOrder(1L);

        verify(orderRepository).deleteById(1L);
        // 결제 전(PENDING) 주문 삭제 시 재고 복구
        verify(productRepository).increaseStock(1L, 1L);
    }

    @Test
    void deleteOrder_paidOrderKeepsStockTest(){
        Order existOrder = Order.builder()
                .user(createUser())
                .orderItemList(List.of(new OrderItem(1L, 1000L, 1L)))
                .orderStatus(OrderStatus.PENDING)
                .build();
        existOrder.completePayment("test-payment-key");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existOrder));

        orderService.deleteOrder(1L);

        verify(orderRepository).deleteById(1L);
        verify(productRepository, never()).increaseStock(anyLong(), anyLong());
    }

    @Test
    void deleteOrder_orderNotFoundExceptionTest(){
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.deleteOrder(999L));
    }
}
