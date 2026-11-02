package com.shopping_mall_api.repository.likes;

import com.shopping_mall_api.entity.likes.Likes;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    boolean existsByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

    long countByProduct_ProductId(Long productId);

    // 찜 목록: 상품을 함께 조회해 상품마다 추가 쿼리가 나가지 않도록
    @EntityGraph(attributePaths = "product")
    List<Likes> findByUser_UserIdOrderByIdDesc(Long userId);

    // 엔티티를 하나씩 읽어 지우지 않고 DELETE 한 번으로 처리
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Likes l where l.user.userId = :userId and l.product.productId = :productId")
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Likes l where l.product.productId = :productId")
    int deleteAllByProductId(@Param("productId") Long productId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Likes l where l.user.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}
