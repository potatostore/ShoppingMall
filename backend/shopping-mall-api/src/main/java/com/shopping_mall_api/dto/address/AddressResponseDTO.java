package com.shopping_mall_api.dto.address;

import com.shopping_mall_api.entity.address.Address;

public record AddressResponseDTO(
        Long addressId,
        String label,
        String recipientName,
        String phoneNumber,
        String zipCode,
        String address1,
        String address2,
        boolean isDefault
) {
    public static AddressResponseDTO from(Address address){
        throw new UnsupportedOperationException("TODO");
    }
}
