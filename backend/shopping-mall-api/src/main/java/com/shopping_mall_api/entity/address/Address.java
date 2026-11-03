package com.shopping_mall_api.entity.address;

import com.shopping_mall_api.entity.user.User;

// [뼈대] 회원 배송지(주소록)
// 구현 시: @Entity, @Table(name = "address"), @Getter, @NoArgsConstructor(access = PROTECTED), BaseEntity 상속
//         user는 @ManyToOne(fetch = LAZY), 회원당 기본 배송지는 1개만 (isDefault)
public class Address {
    private Long addressId;
    private User user;
    private String label;           // 집, 회사 등
    private String recipientName;   // 받는 사람
    private String phoneNumber;
    private String zipCode;
    private String address1;        // 도로명 주소
    private String address2;        // 상세 주소
    private boolean isDefault;

    // 정보 수정 (null·빈 값은 변경 안 함 → User.patchUser와 같은 규칙)
    public void update(String label, String recipientName, String phoneNumber,
                       String zipCode, String address1, String address2){
        throw new UnsupportedOperationException("TODO");
    }

    public void markDefault(){
        throw new UnsupportedOperationException("TODO");
    }

    public void unmarkDefault(){
        throw new UnsupportedOperationException("TODO");
    }
}
