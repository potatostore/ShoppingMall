package com.shopping_mall_api.dto.order;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelDTO {
    // 토스 결제 취소 API의 cancelReason(필수, 최대 200자). 비우면 기본 사유 사용
    @Size(max = 200, message = "cancelReason must be at most 200 characters")
    private String cancelReason;
}
