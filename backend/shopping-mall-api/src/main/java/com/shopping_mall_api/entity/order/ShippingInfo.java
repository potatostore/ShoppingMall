package com.shopping_mall_api.entity.order;

import com.shopping_mall_api.entity.address.Address;

// [뼈대] 주문 시점 배송지 스냅샷 (주소록을 나중에 고치거나 지워도 주문의 배송지는 그대로 유지)
// 구현 시: @Embeddable, Order에 @Embedded private ShippingInfo shippingInfo; 추가
public class ShippingInfo {
    private String recipientName;
    private String phoneNumber;
    private String zipCode;
    private String address1;
    private String address2;
    private String deliveryRequest;  // 배송 요청사항 (문 앞에 놓아주세요 등)

    // 주소록 항목을 복사해 스냅샷 생성
    public static ShippingInfo from(Address address, String deliveryRequest){
        throw new UnsupportedOperationException("TODO");
    }
}
