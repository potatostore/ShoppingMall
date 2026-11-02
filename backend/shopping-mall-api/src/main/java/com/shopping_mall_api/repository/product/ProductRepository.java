package com.shopping_mall_api.repository.product;

import com.shopping_mall_api.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 상품명 부분 일치(대소문자 무시) + 가격 범위. null인 조건은 무시. keyword의 %, _는 서비스에서 '!'로 이스케이프
    // (역슬래시를 쓰면 MySQL 기본 모드에서 문자열 이스케이프로 해석되어 SQL 오류가 남)
    @Query("""
            select p from Product p
            where (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%')) escape '!')
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
            """)
    Page<Product> search(@Param("keyword") String keyword,
                         @Param("minPrice") Long minPrice,
                         @Param("maxPrice") Long maxPrice,
                         Pageable pageable);

    // 조건부 UPDATE: 재고가 충분할 때만 차감하고 바뀐 행 수(0 또는 1)를 반환
    // 조회 후 엔티티에서 빼는 방식(read-modify-write)은 동시 주문 시 둘 다 같은 재고를 읽어 초과 판매될 수 있음
    // 행 잠금은 UPDATE 순간부터 트랜잭션 종료까지 유지되므로, 여러 상품은 productId 오름차순으로 호출해 교착 상태를 피함
    @Modifying(flushAutomatically = true)
    @Query("update Product p set p.stock = p.stock - :quantity where p.productId = :productId and p.stock >= :quantity")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Long quantity);

    @Modifying(flushAutomatically = true)
    @Query("update Product p set p.stock = p.stock + :quantity where p.productId = :productId")
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Long quantity);
}
