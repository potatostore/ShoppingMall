package com.shopping_mall_api.dto.payment.toss;

import com.shopping_mall_api.global.config.CheckConfig;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record TossPaymentRequestDTO(
    @NotBlank
    String paymentKey,

    @NotBlank
    String orderId,

    @NotNull
    @Min(value = 0)
    Long amount
) {
    @Builder
    public TossPaymentRequestDTO(String paymentKey, String orderId, Long amount){
        CheckConfig.npeAndBlankCheck(paymentKey, "paymentKey");
        CheckConfig.npeAndBlankCheck(orderId, "orderId");
        CheckConfig.npeAndNegativeCheck(amount, "amount");

        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }
}
