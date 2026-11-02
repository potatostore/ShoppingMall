package com.shopping_mall_api.repository;

import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.config.JpaAuditingConfig;
import com.shopping_mall_api.repository.product.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

// 동시 주문 시 조건부 UPDATE가 초과 판매를 막는지 확인 (요청마다 별도 트랜잭션)
@DataJpaTest
@Import(JpaAuditingConfig.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ProductStockConcurrencyTest {
    @Autowired private ProductRepository productRepository;
    @Autowired private PlatformTransactionManager transactionManager;

    @AfterEach
    void cleanUp(){
        productRepository.deleteAll();
    }

    @Test
    void concurrentDecrease_neverOversells() throws Exception{
        Product product = productRepository.save(Product.builder()
                .name("한정판 감자")
                .price(1000L)
                .stock(5L)
                .productDetailCreateDTOList(List.of(new ProductDetailCreateDTO("detail")))
                .build());
        Long productId = product.getProductId();

        int threadCount = 20;
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        AtomicInteger success = new AtomicInteger();

        for(int i = 0; i < threadCount; i++){
            executor.submit(() -> {
                try {
                    ready.await();
                    Integer updated = tx.execute(status -> productRepository.decreaseStock(productId, 1L));
                    if(updated != null && updated == 1){
                        success.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.countDown();
        assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        assertThat(success.get()).isEqualTo(5);
        assertThat(productRepository.findById(productId).orElseThrow().getStock()).isZero();
    }
}
