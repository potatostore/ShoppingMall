package com.shopping_mall_api.service.order;

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
    private final CartRepository cartRepository;

    private final TossClient tossClient;

    @Transactional
    public OrderResponseDTO createOrder(Long userId, List<OrderItemCreateDTO> orderItemCreateDTOList){
        CheckConfig.npeCheck(userId, "userId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User (" + userId + ")"));

        Order createOrder = Order.builder()
                .user(user)
                .orderItemCreateDTOList(orderItemCreateDTOList)
                .orderStatus(OrderStatus.PENDING)
                .build();

        // 재고 차감 로직 추가
        // 쿠폰 및 포인트 차감 로직

        orderRepository.save(createOrder);

        return new OrderResponseDTO(createOrder);
    }

    @Transactional(noRollbackFor = PaymentException.class)
    public OrderResponseDTO authTossPayment(TossPaymentRequestDTO tossPaymentRequestDTO){
        CheckConfig.npeCheck(tossPaymentRequestDTO, "tossPaymentRequestDTO");

        Order tossOrder = orderRepository.findById(Long.valueOf(tossPaymentRequestDTO.orderId()))
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
        return null;
    }

    public List<OrderResponseDTO> getOrdersWithUserId(Long userId){
        return null;
    }

    public OrderResponseDTO getOrder(Long orderId){
        return null;
    }

    public OrderResponseDTO patchOrder(Long orderId, OrderUpdateDTO orderUpdateDTO){
        return null;
    }

    public OrderResponseDTO putOrder(Long orderId, OrderUpdateDTO orderUpdateDTO){
        return null;
    }

    public void deleteOrder(Long orderId){

    }
}
