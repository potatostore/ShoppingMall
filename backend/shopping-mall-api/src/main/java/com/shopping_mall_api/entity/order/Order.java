package com.shopping_mall_api.entity.order;

import com.shopping_mall_api.dto.order.OrderUpdateDTO;
import com.shopping_mall_api.dto.order.orderItem.OrderItemUpdateDTO;
import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.TableNames;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.InvalidOrderStatusException;
import com.shopping_mall_api.global.exception.NotFoundException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Entity
@Table(name = TableNames.orderTableName)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, nullable = false)
    private Long orderUid;

    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderItem> orderItemList;

    // ORDINAL(기본값)은 enum 순서가 바뀌면 기존 데이터 의미가 바뀌므로 문자열로 저장
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus;

    // 토스 승인 시 받은 결제 키. 결제 취소(환불) API 호출에 필요
    @Column(length = 200)
    private String paymentKey;

    @NotNull(message = "totalOrderPrice must not be null")
    @Column(nullable = false)
    @Min(value = 0, message = "totalOrderPrice must not be negative")
    private Long totalOrderPrice;

    // OrderItem은 서비스가 Product 가격을 조회해 만든 뒤 전달 (클라이언트 가격 입력 금지)
    @Builder
    public Order(User user, List<OrderItem> orderItemList, OrderStatus orderStatus) {
        CheckConfig.npeCheck(user, "user");
        CheckConfig.npeAndEmptyCheck(orderItemList, "orderItemList");
        CheckConfig.npeCheck(orderStatus, "orderStatus");

        this.orderItemList = new ArrayList<>();
        orderItemList.forEach(this::addOrderItem);
        this.user = user;
        this.orderUid = generateOrderUid();
        this.orderStatus = orderStatus;
    }

    private static Long generateOrderUid(){
        return ThreadLocalRandom.current().nextLong(100_000L, 1_000_000_000_000L);
    }

    // 저장 전 orderUid가 이미 쓰이고 있으면 서비스가 다시 뽑음 (OrderService.assignUniqueOrderUid)
    public void regenerateOrderUid(){
        this.orderUid = generateOrderUid();
    }

    public void addOrderItem(OrderItem orderItem){
        CheckConfig.npeCheck(orderItem, "orderItem");

        orderItem.assignOrder(this);

        orderItemList.add(orderItem);

        updateTotalOrderPrice();
    }

    public void updateTotalOrderPrice(){
        CheckConfig.npeAndEmptyCheck(this.orderItemList, "orderItemList");

        this.totalOrderPrice = orderItemList.stream()
                .mapToLong(OrderItem::getTotalOrderItemPrice).sum();
    }

    // 결제 전(PENDING) 주문의 수량만 수정. 가격은 주문 시점 가격 유지
    public void patchOrder(OrderUpdateDTO orderUpdateDTO){
        CheckConfig.npeCheck(orderUpdateDTO, "orderUpdateDTO");

        checkPending();

        List<OrderItemUpdateDTO> patchOrderItem = orderUpdateDTO.getOrderItemResponseDTOList();

        CheckConfig.npeAndEmptyCheck(patchOrderItem, "patchOrderItem");

        Map<Long, OrderItem> existingOrderItemMap = this.orderItemList.stream()
                .collect(Collectors.toMap(
                        OrderItem::getProductId,
                        item -> item
                ));

        for(OrderItemUpdateDTO dto : patchOrderItem){
            OrderItem orderItem = existingOrderItemMap.get(dto.getProductId());

            if(orderItem == null){
                throw new NotFoundException(ErrorCode.ORDER_NOT_FOUND);
            }

            orderItem.updateQuantity(dto.getQuantity());
        }

        updateTotalOrderPrice();
    }

    public void completePayment(String paymentKey){
        checkPending();
        CheckConfig.npeAndBlankCheck(paymentKey, "paymentKey");
        this.paymentKey = paymentKey;
        this.orderStatus = OrderStatus.PAID;
    }

    // 결제창 이탈 등으로 일정 시간 결제되지 않은 주문 (OrderExpirationScheduler)
    public void expire(){
        checkPending();
        this.orderStatus = OrderStatus.EXPIRED;
    }

    // 결제 전(PENDING) 또는 결제 완료(PAID) 주문만 취소 가능. PAID는 토스 결제 취소가 성공한 뒤에 호출
    public void cancel(){
        if(this.orderStatus != OrderStatus.PENDING && this.orderStatus != OrderStatus.PAID){
            throw new InvalidOrderStatusException(ErrorCode.ORDER_STATUS_INVALID,
                    "Order cannot be canceled (current : " + this.orderStatus + ")");
        }
        this.orderStatus = OrderStatus.CANCELED;
    }

    public void failPayment(){
        checkPending();
        this.orderStatus = OrderStatus.FAILED;
        //주문 재고 복구
        //쿠폰 및 포인트 복구(미사용 처리)
        //실패 사유(로그 저장)
    }

    private void checkPending(){
        if(this.orderStatus != OrderStatus.PENDING){
            throw new InvalidOrderStatusException(ErrorCode.ORDER_STATUS_INVALID,
                    "Order is not PENDING (current : " + this.orderStatus + ")");
        }
    }
}
