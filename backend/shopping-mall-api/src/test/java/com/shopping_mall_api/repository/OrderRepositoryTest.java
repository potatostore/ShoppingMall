package com.shopping_mall_api.repository;

import com.shopping_mall_api.entity.order.Order;
import com.shopping_mall_api.entity.order.OrderItem;
import com.shopping_mall_api.entity.order.OrderStatus;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.JpaAuditingConfig;
import com.shopping_mall_api.repository.order.OrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class OrderRepositoryTest {
    @Autowired private OrderRepository orderRepository;
    @Autowired private EntityManager em;

    private User saveUser(){
        User user = User.builder()
                .email("expire@gmail.com").logInPassword("pw").name("김민준").role("USER")
                .phoneNumber("01011111111").birthday(LocalDate.of(2026, 8, 27)).build();
        em.persist(user);
        return user;
    }

    private Long saveOrder(User user, OrderStatus status, LocalDateTime createdAt){
        Order order = Order.builder()
                .user(user)
                .orderItemList(List.of(new OrderItem(1L, 1000L, 1L)))
                .orderStatus(OrderStatus.PENDING)
                .build();
        if(status == OrderStatus.PAID){
            order.completePayment("pk");
        }
        orderRepository.save(order);
        em.flush();

        // createdAt은 auditing이 현재 시각으로 채우고 updatable=false라 SQL로 과거 시각을 넣음
        em.createNativeQuery("update orders set created_at = ? where order_id = ?")
                .setParameter(1, createdAt)
                .setParameter(2, order.getOrderId())
                .executeUpdate();
        return order.getOrderId();
    }

    @Test
    void findOrderIdsByStatusCreatedBefore_returnsOnlyStalePending(){
        User user = saveUser();
        LocalDateTime now = LocalDateTime.now();

        Long stalePending = saveOrder(user, OrderStatus.PENDING, now.minusMinutes(40));
        saveOrder(user, OrderStatus.PENDING, now.minusMinutes(5));
        saveOrder(user, OrderStatus.PAID, now.minusMinutes(40));
        em.clear();

        List<Long> ids = orderRepository.findOrderIdsByStatusCreatedBefore(
                OrderStatus.PENDING, now.minusMinutes(30), PageRequest.of(0, 100));

        assertThat(ids).containsExactly(stalePending);
    }
}
