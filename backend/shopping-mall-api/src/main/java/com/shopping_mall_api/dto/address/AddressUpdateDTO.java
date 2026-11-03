package com.shopping_mall_api.dto.address;

// [뼈대] 부분 수정: null·빈 값은 변경 안 함
public record AddressUpdateDTO(
        String label,
        String recipientName,
        String phoneNumber,
        String zipCode,
        String address1,
        String address2
) {
}
