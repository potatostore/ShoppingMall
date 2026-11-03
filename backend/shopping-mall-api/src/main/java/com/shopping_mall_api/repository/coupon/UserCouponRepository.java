package com.shopping_mall_api.repository.coupon;

// [뼈대] 구현 시: extends JpaRepository<UserCoupon, Long>
//   boolean existsByUser_UserIdAndCoupon_CouponId(Long userId, Long couponId);
//   @EntityGraph(attributePaths = "coupon") List<UserCoupon> findByUser_UserIdOrderByUserCouponIdDesc(Long userId);
//   Optional<UserCoupon> findByUserCouponIdAndUser_UserId(Long userCouponId, Long userId);
//   Optional<UserCoupon> findByOrderId(Long orderId);   // 주문 취소 시 복구
public interface UserCouponRepository {
}
