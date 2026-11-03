package com.shopping_mall_api.repository.point;

// [뼈대] 구현 시: extends JpaRepository<PointHistory, Long>
//   Page<PointHistory> findByUser_UserIdOrderByPointHistoryIdDesc(Long userId, Pageable pageable);
//   @Query("select coalesce(sum(p.amount), 0) from PointHistory p where p.user.userId = :userId") long sumAmountByUserId(Long userId);
//   List<PointHistory> findByOrderId(Long orderId);   // 주문 취소 시 복구 대상
public interface PointHistoryRepository {
}
