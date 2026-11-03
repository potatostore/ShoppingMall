package com.shopping_mall_api.dto.address;

// [뼈대] 구현 시: @NotBlank(recipientName, phoneNumber, zipCode, address1), 전화번호 @Pattern(010xxxxxxxx, UserCreateDTO와 동일 규칙)
public record AddressCreateDTO(
        String label,
        String recipientName,
        String phoneNumber,
        String zipCode,
        String address1,
        String address2,
        boolean isDefault
) {
}
