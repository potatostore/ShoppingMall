package com.shopping_mall_api.service.address;

import com.shopping_mall_api.dto.address.AddressCreateDTO;
import com.shopping_mall_api.dto.address.AddressResponseDTO;
import com.shopping_mall_api.dto.address.AddressUpdateDTO;

import java.util.List;

// [뼈대] 배송지(주소록) 관리
// 구현 시: @Service, @RequiredArgsConstructor, @Transactional(readOnly = true) + 쓰기 메서드에 @Transactional
public class AddressService {

    // 첫 배송지이거나 isDefault=true면 기본 배송지로, 기존 기본 배송지는 해제
    public AddressResponseDTO createAddress(Long userId, AddressCreateDTO addressCreateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    public List<AddressResponseDTO> getMyAddresses(Long userId){
        throw new UnsupportedOperationException("TODO");
    }

    // 본인 배송지가 아니면 404 (남의 배송지 존재 여부 숨김)
    public AddressResponseDTO updateAddress(Long userId, Long addressId, AddressUpdateDTO addressUpdateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    // 기본 배송지를 지우면 남은 배송지 중 가장 최근 것을 기본으로
    public void deleteAddress(Long userId, Long addressId){
        throw new UnsupportedOperationException("TODO");
    }

    public AddressResponseDTO setDefaultAddress(Long userId, Long addressId){
        throw new UnsupportedOperationException("TODO");
    }
}
