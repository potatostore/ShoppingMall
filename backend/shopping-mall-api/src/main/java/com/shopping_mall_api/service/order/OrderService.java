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
import com.shopping_mall_api.global.exception.OutOfStockException;
import com.shopping_mall_api.global.exception.PaymentException;
import com.shopping_mall_api.global.exception.UnmatchedPriceException;
import com.shopping_mall_api.repository.cart.CartRepository;
import com.shopping_mall_api.repository.order.OrderRepository;
import com.shopping_mall_api.repository.product.ProductRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

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

        // 재고 차감: quantityByProductId가 TreeMap이라 productId 오름차순으로 잠금 → 동시 주문 간 교착 방지
        // 하나라도 부족하면 예외 → 트랜잭션 롤백으로 앞서 차감한 재고도 원복
        quantityByProductId.forEach(this::decreaseStock);

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

        TossResponse tossResponse;
        try {
            tossResponse = tossClient.confirm(tossPaymentRequestDTO);
        } catch (ResourceAccessException e) {
            // 타임아웃·연결 끊김: 토스는 승인했는데 응답만 못 받았을 수 있으므로 실패로 단정하지 않고 결제 조회로 확인
            log.warn("Toss confirm I/O error, look up payment status (orderUid={})", tossOrder.getOrderUid(), e);
            reconcileWithToss(tossOrder);
            return new OrderResponseDTO(tossOrder);
        }

        switch(tossResponse){
            case Payment payment -> completePayment(tossOrder);
            case TossError tossError -> {
                failPaymentAndRestoreStock(tossOrder);
                // noRollbackFor = PaymentException이라 FAILED 상태와 재고 복구가 함께 커밋됨
                throw new PaymentException(ErrorCode.PAYMENT_FAILED);
            }
        }

        return new OrderResponseDTO(tossOrder);
    }

    // 토스 결제 조회 결과로 PENDING 주문을 맞춤
    // DONE → 결제 완료 처리, ABORTED·EXPIRED → 실패 + 재고 복구, 그 외(조회 실패, 결제 없음, 진행 중) → PENDING 유지 후 502
    // PENDING 유지 시 사용자는 같은 paymentKey로 다시 승인 요청 가능
    private void reconcileWithToss(Order order){
        TossResponse lookup;
        try {
            lookup = tossClient.getPaymentByOrderId(order.getOrderUid());
        } catch (ResourceAccessException e) {
            throw new PaymentException(ErrorCode.TOSS_SERVER_ERROR);
        }

        if(lookup instanceof Payment payment){
            if("DONE".equals(payment.status())){
                completePayment(order);
                return;
            }
            if("ABORTED".equals(payment.status()) || "EXPIRED".equals(payment.status())){
                failPaymentAndRestoreStock(order);
                throw new PaymentException(ErrorCode.PAYMENT_FAILED);
            }
        }

        throw new PaymentException(ErrorCode.TOSS_SERVER_ERROR);
    }

    private void completePayment(Order order){
        order.completePayment();
        removeOrderedItemsFromCart(order);
    }

    private void failPaymentAndRestoreStock(Order order){
        order.failPayment();
        restoreStock(order);
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

        Map<Long, Long> beforeQuantity = order.getOrderItemList().stream()
                .collect(Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity));

        order.patchOrder(orderUpdateDTO);

        // 수량 변경분만큼 재고 조정 (늘면 차감, 줄면 복구). 부족하면 예외 → 롤백으로 수정 내용도 취소
        new TreeMap<>(beforeQuantity).forEach((productId, before) -> {
            Long after = order.getOrderItemList().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst().orElseThrow().getQuantity();

            if(after > before){
                decreaseStock(productId, after - before);
            } else if(after < before){
                productRepository.increaseStock(productId, before - after);
            }
        });

        return new OrderResponseDTO(order);
    }

    @Transactional
    public void deleteOrder(Long orderId){
        CheckConfig.npeCheck(orderId, "orderId");

        Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));

        // 결제 전 주문을 지우면 잡아둔 재고를 돌려줌 (PAID는 판매 완료, FAILED는 이미 복구됨)
        if(order.getOrderStatus() == OrderStatus.PENDING){
            restoreStock(order);
        }

        orderRepository.deleteById(orderId);
    }

    // 결제가 끝난 상품만 장바구니에서 제거 (주문 생성 시점에 지우면 결제 실패·이탈 시 장바구니가 비어 버림)
    private void removeOrderedItemsFromCart(Order order){
        cartRepository.findByUserId(order.getUser().getUserId())
                .ifPresent(cart -> order.getOrderItemList()
                        .forEach(item -> cart.deleteCartItem(item.getProductId())));
    }

    private void decreaseStock(Long productId, Long quantity){
        if(productRepository.decreaseStock(productId, quantity) == 0){
            throw new OutOfStockException(ErrorCode.PRODUCT_OUT_OF_STOCK, "Product is out of stock (" + productId + ")");
        }
    }

    private void restoreStock(Order order){
        order.getOrderItemList().stream()
                .sorted(Comparator.comparing(OrderItem::getProductId))
                .forEach(item -> productRepository.increaseStock(item.getProductId(), item.getQuantity()));
    }
}
