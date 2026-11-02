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
import com.shopping_mall_api.entity.order.OrderItem;
import com.shopping_mall_api.entity.order.OrderStatus;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.client.toss.TossClient;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.InvalidOrderStatusException;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.global.exception.PaymentException;
import com.shopping_mall_api.global.exception.UnmatchedPriceException;
import com.shopping_mall_api.repository.order.OrderRepository;
import com.shopping_mall_api.repository.product.ProductRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final TossClient tossClient;

    @Transactional
    public OrderResponseDTO createOrder(Long userId, OrderCreateDTO orderCreateDTO){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(orderCreateDTO, "orderCreateDTO");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User (" + userId + ")"));

        // 같은 상품이 여러 줄로 오면 수량을 합침 (OrderItem은 productId 기준으로 수정되므로 중복 불가)
        Map<Long, Long> quantityByProductId = new TreeMap<>();
        for(OrderItemCreateDTO dto : orderCreateDTO.getOrderItemCreateDTOList()){
            quantityByProductId.merge(dto.getProductId(), dto.getQuantity(), Long::sum);
        }

        Map<Long, Product> productMap = productRepository.findAllById(quantityByProductId.keySet()).stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));

        // 가격은 클라이언트 값이 아니라 주문 시점의 Product.price로 기록 → 토스 승인 금액 대조의 기준
        List<OrderItem> orderItemList = new ArrayList<>();
        for(Map.Entry<Long, Long> entry : quantityByProductId.entrySet()){
            Product product = productMap.get(entry.getKey());

            if(product == null){
                throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Cannot Found Product (" + entry.getKey() + ")");
            }

            orderItemList.add(OrderItem.builder()
                    .productId(product.getProductId())
                    .curOrderItemPrice(product.getPrice())
                    .quantity(entry.getValue())
                    .build());
        }

        // 재고 차감 로직 추가
        // 쿠폰 및 포인트 차감 로직

        Order createOrder = Order.builder()
                .user(user)
                .orderItemList(orderItemList)
                .orderStatus(OrderStatus.PENDING)
                .build();

        orderRepository.save(createOrder);

        return new OrderResponseDTO(createOrder);
    }

    @Transactional(noRollbackFor = PaymentException.class)
    public OrderResponseDTO authTossPayment(TossPaymentRequestDTO tossPaymentRequestDTO){
        CheckConfig.npeCheck(tossPaymentRequestDTO, "tossPaymentRequestDTO");

        Order tossOrder = orderRepository.findByOrderUidForUpdate(tossPaymentRequestDTO.orderId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));

        if(!tossPaymentRequestDTO.amount().equals(tossOrder.getTotalOrderPrice())){
            throw new UnmatchedPriceException(ErrorCode.ORDER_PRICE_UNMATCHED);
        }

        // 토스 API 호출 전에 상태 확인: 이미 결제된 주문의 재승인 요청(새로고침, 버튼 재클릭)이
        // 토스 중복 승인 에러 → failPayment()로 PAID 주문을 FAILED로 덮어쓰던 문제 방지
        switch(tossOrder.getOrderStatus()){
            case PAID -> {
                return new OrderResponseDTO(tossOrder);
            }
            case PENDING -> { }
            default -> throw new InvalidOrderStatusException(ErrorCode.ORDER_STATUS_INVALID,
                    "Order cannot be paid (current : " + tossOrder.getOrderStatus() + ")");
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

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        List<Order> orderList = orderRepository.findByUser_UserId(userId);

        return orderList.stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    public OrderResponseDTO getOrderWithUserId(Long userId, Long orderId){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(orderId, "orderId");

        // 본인 주문만 조회 (남의 주문이면 존재 여부를 숨기기 위해 404)
        return orderRepository.findByOrderIdAndUser_UserId(orderId, userId)
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

        orderRepository.findById(orderId)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));

        orderRepository.deleteById(orderId);
    }
}
