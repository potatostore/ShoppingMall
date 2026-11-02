package com.shopping_mall_api.repository;

import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailUpdateDTO;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.config.JpaAuditingConfig;
import com.shopping_mall_api.repository.product.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Product 상세 목록 교체 회귀 테스트 (copilot-addendum 20261007 이번주 구현 목표 5번)
@DataJpaTest
@Import(JpaAuditingConfig.class)
class ProductRepositoryTest {
    @Autowired private ProductRepository productRepository;
    @Autowired private EntityManager em;

    private Long saveProduct(){
        Product product = Product.builder()
                .name("감자")
                .price(1000L)
                .productDetailCreateDTOList(List.of(new ProductDetailCreateDTO("old detail")))
                .build();

        productRepository.save(product);
        em.flush();
        em.clear();

        return product.getProductId();
    }

    private Long countDetails(Long productId){
        return em.createQuery(
                        "select count(d) from ProductDetail d where d.product.productId = :id", Long.class)
                .setParameter("id", productId)
                .getSingleResult();
    }

    @Test
    void patchProduct_replaceDetails_keepsForeignKey(){
        Long productId = saveProduct();

        Product product = productRepository.findById(productId).orElseThrow();
        product.patchProduct(ProductUpdateDTO.builder()
                .productDetailUpdateDTOList(List.of(
                        new ProductDetailUpdateDTO("new detail 1"),
                        new ProductDetailUpdateDTO("new detail 2")))
                .build());
        em.flush();
        em.clear();

        // 수정 전에는 새 상세의 product_id가 NULL로 저장되어 0건이 조회됨
        assertThat(countDetails(productId)).isEqualTo(2L);
        assertThat(productRepository.findById(productId).orElseThrow().getProductDetailList())
                .extracting("detail")
                .containsExactlyInAnyOrder("new detail 1", "new detail 2");
    }

    @Test
    void putProduct_replaceDetails_keepsForeignKey(){
        Long productId = saveProduct();

        Product product = productRepository.findById(productId).orElseThrow();
        product.putProduct(ProductUpdateDTO.builder()
                .name("고구마")
                .price(2000L)
                .productDetailUpdateDTOList(List.of(new ProductDetailUpdateDTO("put detail")))
                .build());
        em.flush();
        em.clear();

        assertThat(countDetails(productId)).isEqualTo(1L);
        Product result = productRepository.findById(productId).orElseThrow();
        assertThat(result.getName()).isEqualTo("고구마");
        assertThat(result.getProductDetailList()).extracting("detail").containsExactly("put detail");
    }
}
