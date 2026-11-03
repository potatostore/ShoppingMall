package com.shopping_mall_api.repository.coupon;

// [뼈대] 구현 시: extends JpaRepository<Coupon, Long>
// 선착순 발급 수량 차감은 재고 차감(ProductRepository.decreaseStock)과 같은 조건부 UPDATE로
//   @Modifying @Query("update Coupon c set c.issuedQuantity = c.issuedQuantity + 1
//                      where c.couponId = :couponId and (c.totalQuantity is null or c.issuedQuantity < c.totalQuantity)")
//   int increaseIssuedQuantity(Long couponId);   // 0이면 소진
// 발급 가능 목록: List<Coupon> findByStartAtBeforeAndEndAtAfter(LocalDateTime now1, LocalDateTime now2);
public interface CouponRepository {
}
