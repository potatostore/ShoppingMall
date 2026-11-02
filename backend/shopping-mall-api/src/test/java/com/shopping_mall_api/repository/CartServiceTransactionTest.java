package com.shopping_mall_api.repository;

import com.shopping_mall_api.entity.cart.Cart;
import com.shopping_mall_api.global.config.JpaAuditingConfig;
import com.shopping_mall_api.repository.cart.CartRepository;
import com.shopping_mall_api.service.cart.CartService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

// 서비스 메서드의 실제 트랜잭션 설정(readOnly 여부)을 확인하려고 테스트 자체 트랜잭션은 끔
@DataJpaTest
@Import({JpaAuditingConfig.class, CartService.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CartServiceTransactionTest {
    @Autowired private CartService cartService;
    @Autowired private CartRepository cartRepository;

    @AfterEach
    void cleanUp(){
        cartRepository.deleteAll();
    }

    @Test
    void deleteCart_actuallyDeletesRow(){
        cartRepository.save(Cart.builder().userId(1L).build());

        cartService.deleteCart(1L);

        assertThat(cartRepository.findByUserId(1L)).isEmpty();
    }
}
