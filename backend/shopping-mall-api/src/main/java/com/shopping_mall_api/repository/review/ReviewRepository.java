package com.shopping_mall_api.repository.review;

// [뼈대] 구현 시: extends JpaRepository<Review, Long>
//   boolean existsByOrderItemId(Long orderItemId);
//   Page<Review> findByProduct_ProductIdOrderByReviewIdDesc(Long productId, Pageable pageable);  // + @EntityGraph("user")
//   @Query("select new ...RatingSummaryDTO(:productId, coalesce(avg(r.rating), 0), count(r)) from Review r where r.product.productId = :productId")
//   Optional<Review> findByReviewIdAndUser_UserId(Long reviewId, Long userId);
public interface ReviewRepository {
}
