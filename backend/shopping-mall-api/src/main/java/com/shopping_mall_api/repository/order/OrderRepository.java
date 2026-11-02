package com.shopping_mall_api.repository.order;

import com.shopping_mall_api.entity.order.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findByUser_UserId(Long userId);
    public Optional<Order> findByOrderUid(Long orderUid);
    public Optional<Order> findByOrderIdAndUser_UserId(Long orderId, Long userId);

    // 결제 승인용: 같은 주문에 승인 요청이 동시에 들어와도 한 요청씩 상태를 보고 처리하도록 행 잠금 (SELECT ... FOR UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.orderUid = :orderUid")
    public Optional<Order> findByOrderUidForUpdate(@Param("orderUid") Long orderUid);

    // 주문 취소용: 본인 주문만, 승인·만료 처리와 동시에 일어나지 않도록 행 잠금
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.orderId = :orderId and o.user.userId = :userId")
    public Optional<Order> findByOrderIdAndUserIdForUpdate(@Param("orderId") Long orderId, @Param("userId") Long userId);
}
