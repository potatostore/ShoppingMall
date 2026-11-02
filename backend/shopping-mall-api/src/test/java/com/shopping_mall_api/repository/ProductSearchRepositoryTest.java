package com.shopping_mall_api.repository;

import com.shopping_mall_api.dto.product.ProductResponseDTO;
import com.shopping_mall_api.dto.product.ProductSortType;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.config.JpaAuditingConfig;
import com.shopping_mall_api.repository.product.ProductRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 통계로 실행된 SQL 수를 세기 위해 테스트에서만 generate_statistics 사용
@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Import(JpaAuditingConfig.class)
class ProductSearchRepositoryTest {
    @Autowired private ProductRepository productRepository;
    @Autowired private EntityManager em;

    @BeforeEach
    void setUp(){
        save("감자", 1000L);
        save("왕감자", 3000L);
        save("고구마", 2000L);
        save("50%_할인 감자", 500L);
        save("Apple", 4000L);
        em.flush();
        em.clear();
    }

    private void save(String name, Long price){
        productRepository.save(Product.builder()
                .name(name)
                .price(price)
                .stock(10L)
                .productDetailCreateDTOList(List.of(new ProductDetailCreateDTO(name + " 상세 1"), new ProductDetailCreateDTO(name + " 상세 2")))
                .build());
    }

    private List<String> names(Page<Product> page){
        return page.getContent().stream().map(Product::getName).toList();
    }

    @Test
    void keywordAndPriceRange(){
        Page<Product> page = productRepository.search("감자", 800L, 3000L,
                PageRequest.of(0, 10, ProductSortType.PRICE_ASC.getSort()));

        assertThat(names(page)).containsExactly("감자", "왕감자");
    }

    @Test
    void keywordIsCaseInsensitive(){
        Page<Product> page = productRepository.search("apple", null, null,
                PageRequest.of(0, 10, ProductSortType.NAME.getSort()));

        assertThat(names(page)).containsExactly("Apple");
    }

    @Test
    void escapedWildcardMatchesLiterally(){
        // "%_"를 이스케이프하지 않으면 모든 상품에 매칭됨
        Page<Product> page = productRepository.search("!%!_", null, null,
                PageRequest.of(0, 10, ProductSortType.NAME.getSort()));

        assertThat(names(page)).containsExactly("50%_할인 감자");
    }

    @Test
    void pagingAndSort(){
        Page<Product> first = productRepository.search(null, null, null,
                PageRequest.of(0, 2, ProductSortType.PRICE_DESC.getSort()));
        Page<Product> last = productRepository.search(null, null, null,
                PageRequest.of(2, 2, ProductSortType.PRICE_DESC.getSort()));

        assertThat(names(first)).containsExactly("Apple", "왕감자");
        assertThat(first.getTotalElements()).isEqualTo(5);
        assertThat(first.getTotalPages()).isEqualTo(3);
        assertThat(names(last)).containsExactly("50%_할인 감자");
        assertThat(last.hasNext()).isFalse();
    }

    @Test
    void detailsLoadedInBatch_noNPlusOne(){
        Statistics stats = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        Page<Product> page = productRepository.search(null, null, null,
                PageRequest.of(0, 10, ProductSortType.LATEST.getSort()));
        List<ProductResponseDTO> dtos = page.getContent().stream().map(ProductResponseDTO::new).toList();

        assertThat(dtos).hasSize(5);
        assertThat(dtos).allSatisfy(dto -> assertThat(dto.getProductDetailResponseDTOList()).hasSize(2));
        // 목록 1 + 상세 IN 쿼리 1 (+ 마지막 페이지가 아니면 count 1). @BatchSize 없으면 상품 수만큼 상세 쿼리가 추가됨
        assertThat(stats.getPrepareStatementCount()).isLessThanOrEqualTo(3);
    }
}
