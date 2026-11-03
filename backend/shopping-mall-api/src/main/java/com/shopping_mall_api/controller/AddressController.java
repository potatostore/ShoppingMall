package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.address.AddressCreateDTO;
import com.shopping_mall_api.dto.address.AddressResponseDTO;
import com.shopping_mall_api.dto.address.AddressUpdateDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

// [뼈대] 구현 시: @RestController, @RequiredArgsConstructor, @CrossOrigin(기존 컨트롤러와 동일)
//         ApiURLNames에 경로 상수 추가, SecurityConfig에 로그인 필요(authenticated) 등록
public class AddressController {

    // POST /users/me/addresses
    public ResponseEntity<ApiResponse<AddressResponseDTO>> createAddress(Long userId, AddressCreateDTO addressCreateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    // GET /users/me/addresses
    public ResponseEntity<ApiResponse<List<AddressResponseDTO>>> getMyAddresses(Long userId){
        throw new UnsupportedOperationException("TODO");
    }

    // PATCH /users/me/addresses/{addressId}
    public ResponseEntity<ApiResponse<AddressResponseDTO>> updateAddress(Long userId, Long addressId, AddressUpdateDTO addressUpdateDTO){
        throw new UnsupportedOperationException("TODO");
    }

    // DELETE /users/me/addresses/{addressId}
    public void deleteAddress(Long userId, Long addressId){
        throw new UnsupportedOperationException("TODO");
    }

    // PATCH /users/me/addresses/{addressId}/default
    public ResponseEntity<ApiResponse<AddressResponseDTO>> setDefaultAddress(Long userId, Long addressId){
        throw new UnsupportedOperationException("TODO");
    }
}
