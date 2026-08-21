package com.shopping_mall_api.service.order;

import com.shopping_mall_api.dto.order.OrderCreateDTO;
import com.shopping_mall_api.dto.order.OrderResponseDTO;
import com.shopping_mall_api.dto.order.OrderUpdateDTO;
import com.shopping_mall_api.dto.order.orderItem.OrderItemCreateDTO;
import com.shopping_mall_api.dto.payment.toss.TossPaymentRequestDTO;
import com.shopping_mall_api.dto.payment.toss.response.Payment;
import com.shopping_mall_api.dto.payment.toss.response.TossError;
import com.shopping_mall_api.dto.payment.toss.response.TossResponse;
import com.shopping_mall_api.entity.order.Order;
import com.shopping_mall_api.entity.order.OrderStatus;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.client.toss.TossClient;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.global.exception.PaymentException;
import com.shopping_mall_api.global.exception.UnmatchedPriceException;
import com.shopping_mall_api.repository.cart.CartRepository;
import com.shopping_mall_api.repository.order.OrderRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private final TossClient tossClient;

    @Transactional
    public OrderResponseDTO createOrder(Long userId, OrderCreateDTO orderCreateDTO){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(orderCreateDTO, "orderCreateDTO");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User (" + userId + ")"));

        // 주문 생성 전 재고 확인
        // orderCreateDTO curItemPrice필드를 제거하고, 상품 가격은 조회 후 기입

        Order createOrder = Order.builder()
                .user(user)
                .orderItemCreateDTOList(orderCreateDTO.getOrderItemCreateDTOList())
                .orderStatus(OrderStatus.PENDING)
                .build();

        // 주문 생성 시 장바구니에서 해당 상품 제거 로직
        // 재고 차감 로직 추가
        // 쿠폰 및 포인트 차감 로직

        orderRepository.save(createOrder);

        return new OrderResponseDTO(createOrder);
    }

    @Transactional(noRollbackFor = PaymentException.class)
    public OrderResponseDTO authTossPayment(TossPaymentRequestDTO tossPaymentRequestDTO){
        CheckConfig.npeCheck(tossPaymentRequestDTO, "tossPaymentRequestDTO");

        Order tossOrder = orderRepository.findByOrderUid(tossPaymentRequestDTO.orderId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));

        if(!tossPaymentRequestDTO.amount().equals(tossOrder.getTotalOrderPrice())){
            throw new UnmatchedPriceException(ErrorCode.ORDER_PRICE_UNMATCHED);
        }

        TossResponse tossResponse = tossClient.confirm(tossPaymentRequestDTO);

        switch(tossResponse){
            case Payment payment ->{
                tossOrder.completePayment();
            }
            case TossError tossError ->{
                tossOrder.failPayment();
                throw new PaymentException(ErrorCode.PAYMENT_FAILED);
            }
        }

        return new OrderResponseDTO(tossOrder);
    }

    public List<OrderResponseDTO> getOrders(){
        return orderRepository.findAll().stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    public List<OrderResponseDTO> getOrdersWithUserId(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        List<Order> orderList = orderRepository.findByUser_UserId(userId);

        return orderList.stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    public OrderResponseDTO getOrderWithUserId(Long userId, Long orderId){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(orderId, "orderId");

        List<Order> orderList = orderRepository.findByUser_UserId(userId);

        return orderList.stream()
                .filter(order -> order.getOrderId()
                .equals(orderId)).findFirst()
                .map(OrderResponseDTO::new)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Transactional
    public OrderResponseDTO patchOrder(Long userId, Long orderId, OrderUpdateDTO orderUpdateDTO){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(orderId, "orderId");
        CheckConfig.npeCheck(orderUpdateDTO, "orderupdateDTO");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));

        if(!order.getUser().getUserId().equals(userId)){
            throw new NotFoundException(ErrorCode.ORDER_NOT_FOUND);
        }

        order.patchOrder(orderUpdateDTO);

        return new OrderResponseDTO(order);
    }

    @Transactional
    public void deleteOrder(Long orderId){
        CheckConfig.npeCheck(orderId, "orderId");

        orderRepository.deleteById(orderId);
    }
}
