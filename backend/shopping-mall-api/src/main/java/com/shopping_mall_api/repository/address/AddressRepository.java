package com.shopping_mall_api.repository.address;

// [뼈대] 구현 시: extends JpaRepository<Address, Long>
// 필요한 조회
//   List<Address> findByUser_UserIdOrderByIsDefaultDescAddressIdDesc(Long userId);  // 기본 배송지 먼저
//   Optional<Address> findByAddressIdAndUser_UserId(Long addressId, Long userId);  // 본인 배송지만
//   Optional<Address> findByUser_UserIdAndIsDefaultTrue(Long userId);
//   long countByUser_UserId(Long userId);                                          // 배송지 개수 제한(예: 10개)
public interface AddressRepository {
}
